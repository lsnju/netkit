/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v4.Socks4ServerDecoder;
import io.netty.handler.codec.socksx.v4.Socks4ServerEncoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import ls.demon.netkit.agent.socks.AgentSocksHandler;
import ls.demon.netkit.util.SocksServerUtils;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: AgentForwardHandler.java, v 0.1 2017年8月2日 下午2:26:33 song.li@witontek.com Exp $
 */
public class AgentForwardHandler extends ByteToMessageDecoder {
    /**
    * Logger for this class
    */
    private static final Logger       logger = LoggerFactory.getLogger(AgentForwardHandler.class);

    private final Socks5ServerEncoder socks5encoder;

    /**
     * 
     */
    public AgentForwardHandler() {
        this(Socks5ServerEncoder.DEFAULT);
    }

    /**
     * @param socks5encoder
     */
    public AgentForwardHandler(Socks5ServerEncoder socks5encoder) {
        if (socks5encoder == null) {
            throw new NullPointerException("socks5encoder");
        }
        this.socks5encoder = socks5encoder;
    }

    /** 
     * @see io.netty.handler.codec.ByteToMessageDecoder#decode(io.netty.channel.ChannelHandlerContext, io.netty.buffer.ByteBuf, java.util.List)
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in,
                          List<Object> out) throws Exception {
        logger.debug("{} {}", ctx, in);
        final int readerIndex = in.readerIndex();
        if (in.writerIndex() == readerIndex) {
            return;
        }

        ChannelPipeline p = ctx.pipeline();
        final byte versionVal = in.getByte(readerIndex);
        SocksVersion version = SocksVersion.valueOf(versionVal);

        switch (version) {
            case SOCKS4a:
                logKnownVersion(ctx, version);
                p.addAfter(ctx.name(), null, AgentSocksHandler.INSTANCE);
                p.addAfter(ctx.name(), null, Socks4ServerEncoder.INSTANCE);
                p.addAfter(ctx.name(), null, new Socks4ServerDecoder());
                p.remove(this);
                return;
            case SOCKS5:
                p.addAfter(ctx.name(), null, AgentSocksHandler.INSTANCE);
                logKnownVersion(ctx, version);
                p.addAfter(ctx.name(), null, socks5encoder);
                p.addAfter(ctx.name(), null, new Socks5InitialRequestDecoder());
                p.remove(this);
                return;
            default:
                logUnknownVersion(ctx, versionVal);
                break;
        }

        String line = getFirstLine(in);
        if (line == null) {
            return;
        }
        logger.info("{}", line);

        String[] items = StringUtils.split(line, ' ');
        if (items.length != 3) {
            in.skipBytes(in.readableBytes());
            ctx.close();
            return;
        }

        switch (items[0]) {
            case "GET":
            case "POST":
                httpProxy(ctx, in, items);
                break;
            case "CONNECT":
                httpsProxy(ctx, in, items);
                break;
            default:
                in.skipBytes(in.readableBytes());
                ctx.close();
        }

        return;
    }

    /**
     * 
     * @param items
     */
    private void httpsProxy(ChannelHandlerContext ctx, ByteBuf in, String[] items) {
        String[] hp = StringUtils.split(items[1], ':');
        String host = hp[0];
        int port = Integer.parseInt(hp[1]);

        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    logger.info("https代理外部连接已建立 {}", outboundChannel);

                    ChannelFuture responseFuture = ctx.channel().writeAndFlush(getConnectOk());
                    responseFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            ctx.pipeline().remove(AgentForwardHandler.this);
                            outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                            ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                        }
                    });
                } else {
                    logger.info("https代理外部连接已建立失败 {}", outboundChannel);
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }

            }
        });

        Bootstrap b = new Bootstrap();
        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop()).channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true).handler(new DirectClientHandler(promise));

        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    logger.info("https代理外部连接已建立 {}", future.channel());
                } else {
                    // Close the connection if the connection attempt has failed.
                    logger.info("https代理外部连接已建立失败 {}", future.channel());
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });

    }

    private ByteBuf getConnectOk() {
        return Unpooled.wrappedBuffer("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
    }

    /**
     * 
     * @param ctx
     * @param in
     * @param items
     * @throws URISyntaxException
     */
    private void httpProxy(ChannelHandlerContext ctx, ByteBuf in,
                           String[] items) throws URISyntaxException {
        URI uri = new URI(items[1]);
        String host = uri.getHost();
        int port = uri.getPort() != -1 ? uri.getPort() : 80;

        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    logger.info("http代理外部连接已建立 {}", outboundChannel);

                    ctx.pipeline().remove(AgentForwardHandler.this);
                    ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                    outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));

                } else {
                    logger.info("http代理外部连接已建立失败 {}", outboundChannel);
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }

            }
        });

        Bootstrap b = new Bootstrap();
        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop()).channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true).handler(new DirectClientHandler(promise));

        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    logger.info("http代理外部连接已建立 {}", future.channel());
                    future.channel().writeAndFlush(in);
                } else {
                    // Close the connection if the connection attempt has failed.
                    logger.info("http代理外部连接已建立失败 {}", future.channel());
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });
    }

    private String getFirstLine(ByteBuf in) {
        int total = in.readableBytes();
        StringBuilder buff = new StringBuilder(32);
        for (int i = in.readerIndex(); i < total; i++) {
            byte value = in.getByte(i);
            char nextByte = (char) (value & 0xFF);
            if (nextByte == HttpConstants.CR) {
                return buff.toString();
            }
            if (nextByte == HttpConstants.LF) {
                return buff.toString();
            }
            buff.append(nextByte);
            if (buff.length() > 4096) {
                logger.warn("header length over 4k");
                return "error";
            }
        }
        return null;
    }

    private static void logKnownVersion(ChannelHandlerContext ctx, SocksVersion version) {
        logger.debug("{} Protocol version: {}({})", ctx.channel(), version);
    }

    private static void logUnknownVersion(ChannelHandlerContext ctx, byte versionVal) {
        if (logger.isDebugEnabled()) {
            logger.debug("{} Unknown protocol version: {}", ctx.channel(), versionVal & 0xFF);
        }
    }
}

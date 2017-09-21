/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent.http;

import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import ls.demon.netkit.agent.common.DirectClientHandler;
import ls.demon.netkit.agent.common.RelayHandler;
import ls.demon.netkit.util.SocksServerUtils;

/**
 * 
 * @author lisong
 * @version $Id: HttpRequestHandler.java, v 0.1 2017年8月4日 下午10:07:08 lisong Exp $
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final Bootstrap     b      = new Bootstrap();

    /** 
     * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("{} {}", ctx.channel(), msg.getClass());
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;

            logger.debug("{}", req.headers());
            logger.debug("{}", req.content());
            logger.debug("{}", req);

            HttpMethod method = req.method();
            if (method == HttpMethod.CONNECT) {
                String[] hp = StringUtils.split(req.uri(), ':');
                String host = hp[0];
                int port = Integer.parseInt(hp[1]);

                httpsProxy(ctx, host, port);
                return;
            } else if (method == HttpMethod.GET || method == HttpMethod.POST) {
                String subUri = StringUtils.substring(req.uri(), 7);
                String hostInfo = StringUtils.substring(subUri, 0,
                    StringUtils.indexOf(subUri, '/'));

                String[] hp = StringUtils.split(hostInfo, ':');
                String host = hp[0];
                int port = hp.length == 2 ? Integer.parseInt(hp[1]) : 80;

                httpProxy(ctx, req, host, port);
                return;
            } else {
                SocksServerUtils.closeOnFlush(ctx.channel());
            }

        }
    }

    private void httpsProxy(final ChannelHandlerContext ctx, final String host, final int port) {
        logger.info("httpsProxy {}:{} {}", host, port, ctx.channel());

        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    // logger.info("https代理外部连接已建立 {}", outboundChannel);

                    ChannelFuture responseFuture = ctx.channel()
                        .writeAndFlush(SocksServerUtils.getConnectOk());
                    responseFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {

                            //logger.info("{}", PipeUtils.toStr(ctx.pipeline()));
                            ctx.pipeline().remove(HttpRequestDecoder.class);
                            ctx.pipeline().remove(HttpObjectAggregator.class);
                            ctx.pipeline().remove(HttpRequestHandler.this);
                            outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                            ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                            //logger.info("{}", PipeUtils.toStr(ctx.pipeline()));
                        }
                    });
                } else {
                    logger.warn("https代理外部连接建立失败 {}:{} {}", host, port, outboundChannel);
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }

            }
        });

        //        Bootstrap b = new Bootstrap();
        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop()).channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true).handler(new DirectClientHandler(promise));

        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    // logger.info("https代理外部连接已建立 {}", future.channel());
                } else {
                    // Close the connection if the connection attempt has failed.
                    logger.warn("https代理外部连接建立失败 {}:{} {}", host, port, future.channel());
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });
        logger.info("httpsProxy end {}:{} {}", host, port, ctx.channel());
    }

    /**
     * 
     * @param ctx
     * @param in
     * @param items
     * @throws URISyntaxException
     */
    private void httpProxy(final ChannelHandlerContext ctx, FullHttpRequest req, final String host,
                           final int port) {
        logger.info("httpProxy {}:{} {}", host, port, ctx.channel());
        //        final ByteBuf dst = Unpooled.buffer(req.content().readableBytes());
        //        req.content().readBytes(dst);
        //        logger.info("{}", req.content());
        //        logger.info("{}", dst);
        final FullHttpRequest xxReq = req.copy();

        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();

                if (future.isSuccess()) {
                    // logger.info("http代理外部连接已建立 {}", outboundChannel);

                    ctx.pipeline().remove(HttpRequestDecoder.class);
                    ctx.pipeline().remove(HttpObjectAggregator.class);
                    ctx.pipeline().remove(HttpRequestHandler.this);

                    outboundChannel.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                    outboundChannel.pipeline().addLast(new HttpRequestEncoder());
                    outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                    ctx.pipeline().addLast(new RelayHandler(outboundChannel));

                    //                    DefaultFullHttpRequest newReq = new DefaultFullHttpRequest(
                    //                        req.protocolVersion(), req.method(), req.uri());
                    //                    newReq.headers().add(req.headers());
                    // newReq.content().writeBytes(dst);

                    outboundChannel.writeAndFlush(xxReq);

                } else {
                    logger.warn("http代理外部连接建立失败 {}:{} {}", host, port, outboundChannel);
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }

            }
        });

        //        Bootstrap b = new Bootstrap();
        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop()).channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true).handler(new DirectClientHandler(promise));

        b.connect(host, port).addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                    // logger.info("http代理外部连接已建立 {}", future.channel());
                    // https://stackoverflow.com/questions/41556208/io-netty-util-illegalreferencecountexception-refcnt-0-in-netty
                    // future.channel().writeAndFlush(in.retain());
                } else {
                    // Close the connection if the connection attempt has failed.
                    logger.warn("http代理外部连接建立失败 {}:{} {}", host, port, future.channel());
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            }
        });

        logger.info("httpProxy end {}:{} {}", host, port, ctx.channel());
    }

}

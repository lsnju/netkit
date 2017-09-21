/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.client.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;
import ls.demon.netkit.agent.common.DirectClientHandler;
import ls.demon.netkit.agent.common.RelayHandler;
import ls.demon.netkit.util.SocksServerUtils;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: ProxyClientForwardHandler.java, v 0.1 2017年8月8日 下午4:32:38 song.li@witontek.com Exp $
 */
public class ProxyClientForwardHandler extends ChannelInboundHandlerAdapter {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientForwardHandler.class);

    private final Bootstrap     b      = new Bootstrap();

    private static final String host   = "127.0.0.1";
    private static final int    port   = 8888;

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        logger.info("channelActive {}", ctx);

        // proxy(ctx);
        // ctx.pipeline().remove(this);
    }

    /**
     * 
     * @param ctx
     * @throws InterruptedException 
     */
    private void proxy(final ChannelHandlerContext ctx) throws InterruptedException {
        logger.info("proxy {}", ctx);
        Promise<Channel> promise = ctx.executor().newPromise();
        promise.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    logger.info("代理外部连接已建立 {}", outboundChannel);

                    // ctx.pipeline().remove(ProxyClientForwardHandler.this);
                    outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()));
                    ctx.pipeline().addLast(new RelayHandler(outboundChannel));

                    //logger.info("{}", PipeUtils.toStr(ctx.pipeline()));
                } else {
                    logger.warn("https代理外部连接建立失败 {}:{} {}", host, port, outboundChannel);
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }

            }
        });

        final Channel inboundChannel = ctx.channel();
        b.group(inboundChannel.eventLoop()).channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .option(ChannelOption.SO_KEEPALIVE, true).handler(new DirectClientHandler(promise));

        ChannelFuture future = b.connect(host, port).sync();
        if (future.isSuccess()) {
            logger.info("cccccccc {}", future.channel());
        }
        logger.info("proxy over");
    }

    /** 
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRegistered(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info("channelRegistered {}", ctx);
        proxy(ctx);
    }

    /** 
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        logger.info("channelRead {} {}", ctx, msg);
        ctx.fireChannelRead(msg);
    }

    /** 
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelReadComplete(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        logger.info("channelReadComplete {}", ctx);
        ctx.fireChannelReadComplete();
    }

    /** 
     * @see io.netty.channel.ChannelHandlerAdapter#handlerAdded(io.netty.channel.ChannelHandlerContext)
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        logger.info("handlerAdded {}", ctx);

        //  proxy(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        logger.info("eeee", throwable);
    }
}

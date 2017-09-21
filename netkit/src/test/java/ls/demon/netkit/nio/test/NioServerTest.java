/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.nio.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ls.demon.netkit.nio.SocksServerInitializer;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: NioServerTest.java, v 0.1 2017年8月9日 上午10:38:59 song.li@witontek.com Exp $
 */
public class NioServerTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(NioServerTest.class);

    public static void main(String[] args) throws Exception {
        int port = 1111;
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new SocksServerInitializer());
            ChannelFuture f = b.bind(port).sync();
            logger.info("");
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

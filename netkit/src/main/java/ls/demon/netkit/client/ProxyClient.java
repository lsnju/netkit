/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ls.demon.netkit.client.common.ProxyCilentInitializer;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: ProxyClient.java, v 0.1 2017年8月8日 下午4:22:15 song.li@witontek.com Exp $
 */
public class ProxyClient {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ProxyClient.class);

    private int                 PORT   = 1088;

    public ProxyClient(int pORT) {
        PORT = pORT;
    }

    public ProxyClient() {
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ProxyCilentInitializer());

            ChannelFuture f = b.bind(PORT).sync();
            logger.info("listening on port {} ...", PORT);

            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ProxyClient(1088).start();
    }
}

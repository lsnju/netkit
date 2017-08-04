/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ls.demon.netkit.agent.common.ProxyAgentInitializer;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: SocksAgent.java, v 0.1 2017年8月2日 上午8:25:08 song.li@witontek.com Exp $
 */
public class ProxyAgent {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ProxyAgent.class);

    private int                 PORT   = Integer.parseInt(System.getProperty("port", "8888"));

    public ProxyAgent(int pORT) {
        super();
        PORT = pORT;
    }

    public ProxyAgent() {
        super();
    }

    public void start() {

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ProxyAgentInitializer());

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
        new ProxyAgent().start();
    }
}

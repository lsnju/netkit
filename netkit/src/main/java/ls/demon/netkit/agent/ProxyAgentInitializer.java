/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: AgentSocksServerInitializer.java, v 0.1 2017年8月2日 上午8:37:31 song.li@witontek.com Exp $
 */
public class ProxyAgentInitializer extends ChannelInitializer<SocketChannel> {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ProxyAgentInitializer.class);

    /** 
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        logger.info("ch={}", ch);
        logger.info("ch.pipe={}", ch.pipeline());
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG), new AgentForwardHandler());
    }

}

/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.client.common;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: ProxyCilentInitializer.java, v 0.1 2017年8月8日 下午4:30:46 song.li@witontek.com Exp $
 */
public class ProxyCilentInitializer extends ChannelInitializer<SocketChannel> {

    /** 
     * @see io.netty.channel.ChannelInitializer#initChannel(io.netty.channel.Channel)
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO), new ProxyClientForwardHandler());
    }

}

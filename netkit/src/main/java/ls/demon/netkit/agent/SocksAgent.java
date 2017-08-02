/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: SocksAgent.java, v 0.1 2017年8月2日 上午8:25:08 song.li@witontek.com Exp $
 */
public class SocksAgent {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(SocksAgent.class);

    private int                 PORT   = Integer.parseInt(System.getProperty("port", "8888"));

    public SocksAgent(int pORT) {
        super();
        PORT = pORT;
    }

    public SocksAgent() {
        super();
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

    }
}

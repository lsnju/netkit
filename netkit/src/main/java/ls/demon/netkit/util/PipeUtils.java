/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.util;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: PipeUtils.java, v 0.1 2017年8月2日 上午10:23:44 song.li@witontek.com Exp $
 */
public class PipeUtils {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(PipeUtils.class);

    public static void showAll(ChannelPipeline pipeline) {
        logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx-s");
        for (Entry<String, ChannelHandler> e : pipeline) {
            logger.debug("{} = {}", e.getKey(), e.getValue());
        }
        logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx-e");
    }

    public static void showAll(String name, ChannelPipeline pipeline) {
        logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx-{}-start.{}", name, pipeline.hashCode());
        for (Entry<String, ChannelHandler> e : pipeline) {
            logger.debug("{} = {}", e.getKey(), e.getValue());
        }
        logger.debug("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx-{}-end.{}", name, pipeline.hashCode());
    }

    public static String toStr(ChannelPipeline pipeline) {
        StringBuilder sb = new StringBuilder(64);
        for (Entry<String, ChannelHandler> e : pipeline) {
            sb.append(e.getKey()).append(" = ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }

}

/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.buffer;

import java.nio.charset.Charset;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: ByteBufferTest.java, v 0.1 2017年8月2日 下午3:02:45 song.li@witontek.com Exp $
 */
public class ByteBufferTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ByteBufferTest.class);

    @Test
    public void test_heapbuf_001() {
        logger.info("");
        ByteBuf b0 = Unpooled.buffer();
        logger.info("{}", b0);
        logger.info("{}", b0.hasArray());
        logger.info("{}", b0.nioBufferCount());
        logger.info("{}", b0.nioBuffer());

        ByteBuf b1 = Unpooled.buffer(128);
        logger.info("{}", b1);

        b1.capacity(512);
        logger.info("{}", b1);

        logger.info("{}", b1);
        ByteBufUtil.writeUtf8(b1, "中文测试tttt");
        logger.info("{}", b1);
        logger.info("{}", b1.toString(Charset.forName("utf-8")));

        ByteBuf b2 = b1.copy();
        logger.info("{}", b2);

        b1.capacity(13);
        logger.info("{}", b1);

    }

    @Test
    public void test_directbuf_001() {
        logger.info("");
        ByteBuf b0 = Unpooled.directBuffer();
        logger.info("{}", b0);

        ByteBuf b1 = Unpooled.directBuffer(128);
        logger.info("{}", b1);

        b1.capacity(512);
        logger.info("{}", b1);

        logger.info("{}", b1);
        ByteBufUtil.writeUtf8(b1, "中文测试tttt");
        logger.info("{}", b1);

        ByteBuf b2 = b1.copy();
        logger.info("{}", b2);

        b1.capacity(13);
        logger.info("{}", b1);

    }

    @Test
    public void test_compositeBuffer_001() {
        logger.info("");
        CompositeByteBuf b0 = Unpooled.compositeBuffer();
        logger.info("{}", b0);

        CompositeByteBuf b1 = Unpooled.compositeBuffer(128);
        logger.info("{}", b1);

        b1.capacity(512);
        logger.info("{}", b1);

        logger.info("{}", b1);
        ByteBufUtil.writeUtf8(b1, "中文测试tttt");
        logger.info("{}", b1);

        ByteBuf b2 = b1.copy();
        logger.info("{}", b2);

        b1.capacity(13);
        logger.info("{}", b1);

    }
}

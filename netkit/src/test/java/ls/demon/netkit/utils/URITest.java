/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.utils;

import java.net.URI;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: URITest.java, v 0.1 2017年8月3日 下午5:22:23 song.li@witontek.com Exp $
 */
public class URITest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(URITest.class);

    @Test
    public void test_001() {
        try {
            URI u1 = new URI("www.alipay.com:443");
            logger.info("{}", u1);
            logger.info("{}", u1.getHost());
            logger.info("{}", u1.getPort());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Test
    public void test_002() {
        try {
            URI u1 = new URI("http://www.alipay.com/index.htm");
            logger.info("{}", u1);
            logger.info("{}", u1.getHost());
            logger.info("{}", u1.getPort());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Test
    public void test_003() {
        try {
            URI u1 = new URI("http://www.alipay.com:88/index.htm");
            logger.info("{}", u1);
            logger.info("{}", u1.getHost());
            logger.info("{}", u1.getPort());
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}

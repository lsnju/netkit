/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.utils;

import java.net.URLEncoder;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: StringEscapeTest.java, v 0.1 2017年8月2日 下午3:25:36 song.li@witontek.com Exp $
 */
public class StringEscapeTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(StringEscapeTest.class);

    @Test
    public void test_001() {
        try {
            logger.info("{}", StringEscapeUtils.escapeJava("小飞侠"));
            logger.info("{}", StringEscapeUtils.escapeJava("中文"));
            logger.info("{}",
                StringEscapeUtils.unescapeJava("\\u0048\\u0065\\u006C\\u006C\\u006F"));

            logger.info("{}", StringEscapeUtils.escapeJson("中文"));
            logger.info("{}", StringEscapeUtils.escapeHtml4("中文"));
            logger.info("{}", StringEscapeUtils.escapeHtml3("中文"));
            logger.info("{}", URLEncoder.encode("中文", "utf-8"));

            String data = "MERCHANTID=123456789&POSID=000000000&BRANCHID=110000000&ORDERID=19991101234&PAYMENT=0.01&CURCODE=01&TXCODE=530550&REMARK1=&REMARK2=&RETURNTYPE=&TIMEOUT=&PUB=30819d300d06092a864886f70d0108";
            logger.info("{}", DigestUtils.md5Hex(data));
            logger.info("{}", DigestUtils.md5Hex(data.getBytes("gbk")));
            logger.info("{}", DigestUtils.md5Hex(data.getBytes("utf-8")));

            // https://ibsbjstar.ccb.com.cn/CCBIS/ccbMain?CCB_IBSVersion=V6&MERCHANTID=105584073990033&POSID=100000415&BRANCHID=442000000&ORDERID=72133&PAYMENT=0.01&CURCODE=01&TXCODE=530550&REMARK1=&REMARK2=&RETURNTYPE=1&TIMEOUT=&MAC=d2656488823833a239d182541f1a1b16
            String ss = "MERCHANTID=105584073990033&POSID=100000415&BRANCHID=442000000&ORDERID=72133&PAYMENT=0.01&CURCODE=01&TXCODE=530550&REMARK1=&REMARK2=&RETURNTYPE=1&TIMEOUT=&PUB=bd0f9a0658b5640c37378787020111";
            logger.info("{}", DigestUtils.md5Hex(ss));
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}

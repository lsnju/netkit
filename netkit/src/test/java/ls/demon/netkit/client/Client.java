/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ls.demon.netkit.utils.JdkProxyUtils;

/**
 * 
 * @author lisong
 * @version $Id: Client.java, v 0.1 2017年7月29日 上午10:45:40 lisong Exp $
 */
public class Client {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private String              host   = "127.0.0.1";
    private String              port   = "8888";

    @Test
    public void test_http_proxy() {
        logger.info("");
        JdkProxyUtils.httpProxy(host, port);

        try {
            // Next connection will be through proxy.
            URL url = new URL("http://http://cn.bing.com/");
            InputStream in = url.openStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            for (;;) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                logger.info("< {}", line);
            }

            IOUtils.closeQuietly(in);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

}

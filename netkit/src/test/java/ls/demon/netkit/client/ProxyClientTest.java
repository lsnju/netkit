/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.client;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ls.demon.netkit.utils.EhJerseyClient;
import ls.demon.netkit.utils.JdkProxyUtils;

/**
 * 
 * @author lisong
 * @version $Id: Client.java, v 0.1 2017年7月29日 上午10:45:40 lisong Exp $
 */
public class ProxyClientTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(ProxyClientTest.class);

    private String              host   = "127.0.0.1";
    private String              port   = "8888";

    @Test
    public void test_http_proxy() {
        logger.info("");
        JdkProxyUtils.httpProxy(host, port);

        try {
            // Next connection will be through proxy.
            URL url = new URL("http://cn.bing.com/");
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

    @Test
    public void test_https_proxy() {
        logger.info("");
        JdkProxyUtils.httpsProxy(host, port);

        try {
            // Next connection will be through proxy.
            URL url = new URL("https://www.alipay.com/");
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

    @Test
    public void test_jersy_client_http_proxy_xx() {
        logger.info("");
        try {
            Client client = EhJerseyClient.getXX();
            WebTarget wt = client.target("http://cn.bing.com/");
            String resp = wt.request().get(String.class);
            logger.info("{}", resp);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    @Test
    public void test_jersy_client_http_proxy() {
        logger.info("");
        JdkProxyUtils.httpProxy(host, port);
        //        JdkProxyUtils.httpsProxy(host, port);
        //        JdkProxyUtils.socksProxy(host, port);

        try {
            Client client = EhJerseyClient.getJerseyClient();
            WebTarget wt = client.target("http://cn.bing.com/");
            String resp = wt.request().get(String.class);
            logger.info("{}", resp);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    @Test
    public void test_jersy_client_http_post_proxy() {
        logger.info("");
        JdkProxyUtils.httpProxy(host, port);
        //        JdkProxyUtils.httpsProxy(host, port);
        //        JdkProxyUtils.socksProxy(host, port);

        try {
            Client client = EhJerseyClient.getJerseyClient();
            WebTarget wt = client.target("http://cn.bing.com/");
            Form f = new Form();
            f.param("name", "value");
            String resp = wt.request().post(Entity.form(f), String.class);
            logger.info("{}", resp);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    @Test
    public void test_jersy_client_https_proxy() {
        logger.info("");
        JdkProxyUtils.httpProxy(host, port);
        JdkProxyUtils.httpsProxy(host, port);
        JdkProxyUtils.socksProxy(host, port);

        try {
            Client client = EhJerseyClient.getJerseyClient();
            WebTarget wt = client.target("https://www.alipay.com/");
            String resp = wt.request().get(String.class);
            logger.info("{}", resp);
        } catch (Exception e) {
            logger.error("", e);
        }

    }

}

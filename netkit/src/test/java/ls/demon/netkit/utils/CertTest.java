/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.utils;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Enumeration;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lisong
 * @version $Id: CertTest.java, v 0.1 2017年8月5日 上午10:49:13 lisong Exp $
 */
public class CertTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(CertTest.class);

    @Test
    public void test_001() {
        try {
            String path = "D:/TEMP/certs/acp_test_sign.pfx";

            FileInputStream in = new FileInputStream(path);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] charArray = "000000".toCharArray();
            ks.load(in, charArray);

            Enumeration<String> aliasenum = ks.aliases();
            while (aliasenum.hasMoreElements()) {
                String name = aliasenum.nextElement();
                logger.info("{}", name);

                Key key = ks.getKey(name, charArray);
                RSAPrivateKey pk = (RSAPrivateKey) key;
                logger.info("{}", key);
                logger.info("{}", pk);

                Certificate c = ks.getCertificate(name);
                logger.info("{}", Base64.encodeBase64String(c.getEncoded()));

                PublicKey pubK = c.getPublicKey();
                logger.info("{}", Base64.encodeBase64String(pubK.getEncoded()));

            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Test
    public void test_server_ks() {
        try {
            String path = "D:/TEMP/certs/server/server.keystore";

            FileInputStream in = new FileInputStream(path);
            KeyStore ks = KeyStore.getInstance("JKS");
            char[] charArray = "123456".toCharArray();
            ks.load(in, charArray);

            Enumeration<String> aliasenum = ks.aliases();
            while (aliasenum.hasMoreElements()) {
                String name = aliasenum.nextElement();
                logger.info("{}", name);

                Key key = ks.getKey(name, charArray);
                RSAPrivateKey pk = (RSAPrivateKey) key;
                logger.info("{}", key);
                logger.info("{}", pk);

                Certificate c = ks.getCertificate(name);
                logger.info("{}", Base64.encodeBase64String(c.getEncoded()));

                PublicKey pubK = c.getPublicKey();
                logger.info("{}", Base64.encodeBase64String(pubK.getEncoded()));

            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Test
    public void test_client_ks() {
        try {
            String path = "D:/TEMP/certs/client/client.p12";

            FileInputStream in = new FileInputStream(path);
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] charArray = "123456".toCharArray();
            ks.load(in, charArray);

            Enumeration<String> aliasenum = ks.aliases();
            while (aliasenum.hasMoreElements()) {
                String name = aliasenum.nextElement();
                logger.info("{}", name);

                Key key = ks.getKey(name, charArray);
                RSAPrivateKey pk = (RSAPrivateKey) key;
                logger.info("{}", key);
                logger.info("{}", pk);

                Certificate c = ks.getCertificate(name);
                logger.info("{}", Base64.encodeBase64String(c.getEncoded()));

                PublicKey pubK = c.getPublicKey();
                logger.info("{}", Base64.encodeBase64String(pubK.getEncoded()));

            }
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}

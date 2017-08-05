/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ls.demon.netkit.util.CertUtils;

/**
 * 
 * @author lisong
 * @version $Id: KeyStoreTest.java, v 0.1 2017年8月5日 下午3:09:57 lisong Exp $
 */
public class KeyStoreTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(KeyStoreTest.class);

    @Test
    public void test_001() {
        try {

            X509Certificate c = CertUtils.getCertFromFile("D:/TEMP/certs/server/server.cer");

            String ksType = KeyStore.getDefaultType();
            logger.info("{}", ksType);
            KeyStore ks = KeyStore.getInstance(ksType);
            ks.load(null);

            ks.setCertificateEntry(UUID.randomUUID().toString(), c);
            ks.store(new FileOutputStream("D:/TEMP/certs/server/test.ks"), "123456".toCharArray());

        } catch (Exception e) {
            logger.error("", e);
        }
    }

    @Test
    public void test_002() {
        try {
            char[] charArray = "123456".toCharArray();

            String ksType = KeyStore.getDefaultType();
            logger.info("{}", ksType);
            KeyStore ks = KeyStore.getInstance(ksType);
            ks.load(new FileInputStream("D:/TEMP/certs/server/test.ks"), charArray);

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

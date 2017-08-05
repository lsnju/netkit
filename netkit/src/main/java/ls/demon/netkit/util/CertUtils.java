/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.util;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPublicKeySpec;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: CertUtils.java, v 0.1 2017年6月28日 上午9:37:37 song.li@witontek.com Exp $
 */
public class CertUtils {

    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(CertUtils.class);

    /**  */
    //    private static final String BC     = "BC";
    /**  */
    private static final String RSA    = "RSA";
    /**  */
    private static final String X_509  = "X.509";
    /**  */
    private static final String PKCS12 = "PKCS12";

    // =======================================================================
    // =======================================================================

    // =======================================================================
    // =======================================================================

    // =======================================================================
    // =======================================================================

    /**
     * 通过证书路径初始化为公钥证书
     * @param path
     * @return
     */
    public static X509Certificate getCertFromFile(String path) {
        try (FileInputStream in = new FileInputStream(path);) {
            CertificateFactory cf = CertificateFactory.getInstance(X_509);
            X509Certificate encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
            logger.info("[{}][CertId={}]", path, encryptCertTemp.getSerialNumber());
            return encryptCertTemp;
        } catch (Exception e) {
            logger.error("InitCert Error", e);
        }
        return null;
    }

    public static X509Certificate getCertFromStr(String certStr) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(certStr.getBytes("ISO-8859-1"));) {
            CertificateFactory cf = CertificateFactory.getInstance(X_509);
            X509Certificate encryptCertTemp = (X509Certificate) cf.generateCertificate(in);
            logger.info("[CertId={}]", encryptCertTemp.getSerialNumber());
            return encryptCertTemp;
        } catch (Exception e) {
            logger.error("InitCert Error", e);
        }
        return null;
    }

    // =======================================================================
    // =======================================================================

    public static PrivateKey getPrivateKey(String pfxkeyfile, String keypwd) {
        return getPrivateKey(pfxkeyfile, keypwd, PKCS12);
    }

    public static PrivateKey getPrivateKey(String pfxkeyfile, String keypwd, String type) {
        KeyStore ks = getKeyInfo(pfxkeyfile, keypwd, type);
        if (ks == null) {
            return null;
        }
        return getPrivateKey(ks, keypwd);
    }

    public static PrivateKey getPrivateKey(KeyStore ks, String keypwd) {
        try {
            Enumeration<String> aliasenum = ks.aliases();
            if (aliasenum.hasMoreElements()) {
                String alias = aliasenum.nextElement();
                logger.info("{}", alias);
                return (PrivateKey) ks.getKey(alias, keypwd.toCharArray());
            }
            return null;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    // =======================================================================
    // =======================================================================

    public static X509Certificate getCert(String pfxkeyfile, String keypwd) {
        return getCert(pfxkeyfile, keypwd, PKCS12);
    }

    public static X509Certificate getCert(String pfxkeyfile, String keypwd, String type) {
        KeyStore ks = getKeyInfo(pfxkeyfile, keypwd, type);
        if (ks == null) {
            return null;
        }
        return getCert(ks);
    }

    public static X509Certificate getCert(KeyStore ks) {
        try {
            Enumeration<String> aliasenum = ks.aliases();
            if (aliasenum.hasMoreElements()) {
                String alias = aliasenum.nextElement();
                logger.info("{}", alias);
                X509Certificate encryptCertTemp = (X509Certificate) ks.getCertificate(alias);
                logger.info("[CertId={}]", encryptCertTemp.getSerialNumber());
                return encryptCertTemp;
            }
            return null;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    // =======================================================================
    // =======================================================================

    public static KeyStore getKeyInfo(String pfxkeyfile, String keypwd) {
        return getKeyInfo(pfxkeyfile, keypwd, PKCS12);
    }

    public static KeyStore getKeyInfo(String pfxkeyfile, String keypwd, String type) {
        logger.info("Load RSA CertPath=[{}],Pwd=[{}],type=[{}]", pfxkeyfile, keypwd, type);
        try (FileInputStream fis = new FileInputStream(pfxkeyfile);) {
            KeyStore ks = KeyStore.getInstance(type);
            ks.load(fis, keypwd.toCharArray());
            return ks;
        } catch (Exception e) {
            logger.error("getKeyInfo Error", e);
            return null;
        }
    }

    // =======================================================================
    // =======================================================================

    public static PublicKey getPublicKey(String modulus, String exponent) {
        try {
            BigInteger b1 = new BigInteger(modulus);
            BigInteger b2 = new BigInteger(exponent);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            logger.error("构造RSA公钥失败：" + e);
            return null;
        }
    }

}

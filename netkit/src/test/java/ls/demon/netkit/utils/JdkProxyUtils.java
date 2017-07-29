/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.utils;

/**
 * 
 * @author lisong
 * @version $Id: JdkProxyUtils.java, v 0.1 2017年7月29日 上午10:48:41 lisong Exp $
 */
public class JdkProxyUtils {

    public static void httpProxy(String host, String port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
    }

    public static void httpsProxy(String host, String port) {
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
    }

    public static void socksProxy(String host, String port) {
        System.setProperty("socksProxyHost", host);
        System.setProperty("socksProxyPort", port);
    }
}

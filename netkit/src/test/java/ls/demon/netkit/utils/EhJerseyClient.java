/**
 * Witontek.com.
 * Copyright (c) 2012-2016 All Rights Reserved.
 */
package ls.demon.netkit.utils;

import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Feature;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.logging.LoggingFeature.Verbosity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: JerseyClient.java, v 0.1 2016年6月21日 上午11:14:44 song.li@witontek.com Exp $
 */
public class EhJerseyClient {

    /**  */
    private static final String       COM_WITON_JERSEY_CLIENT = "jersey.CLIENT";

    /**
    * Logger for this class
    */
    private static final Logger       logger                  = LoggerFactory
        .getLogger(COM_WITON_JERSEY_CLIENT);

    private static final ClientConfig CLIENT_CONFIG;

    private static final Client       CLIENT;

    public static final String        PROTOCOL                = "TLS";

    public static ClientConfig newClientConfig() {

        ClientConfig config = new ClientConfig();

        config.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_CLIENT,
            LoggingFeature.Verbosity.PAYLOAD_ANY);
        config.property(LoggingFeature.LOGGING_FEATURE_VERBOSITY_SERVER,
            LoggingFeature.Verbosity.PAYLOAD_ANY);

        config.property(ClientProperties.CONNECT_TIMEOUT, 5 * 1000);
        config.property(ClientProperties.READ_TIMEOUT, 10 * 1000);

        Level logLevel = Level.FINE;
        if (logger.isTraceEnabled() || logger.isDebugEnabled()) {
            logLevel = Level.INFO;
        }
        logger.warn("客户端调试日志级别 {}", logLevel);
        // 1 
        java.util.logging.Logger log = java.util.logging.Logger.getLogger(COM_WITON_JERSEY_CLIENT);
        Feature feature = new LoggingFeature(log, logLevel, Verbosity.PAYLOAD_ANY, null);
        config.register(feature);
        return config;
    }

    static {
        CLIENT_CONFIG = newClientConfig();
        CLIENT = ClientBuilder.newClient(CLIENT_CONFIG);
    }

    /**
     * 
     * @return
     */
    public static Client getJerseyClient() {
        return CLIENT;
    }

    public static Client getXX() {

        ClientConfig config = newClientConfig();

        config.connectorProvider(new ApacheConnectorProvider());
        config.property(ClientProperties.PROXY_URI, "http://127.0.0.1:8888");
        //config.property(ClientProperties.PROXY_USERNAME,user);
        //config.property(ClientProperties.PROXY_PASSWORD,pass);
        Client client = JerseyClientBuilder.newClient(config);

        logger.info("{}", CLIENT_CONFIG);
        logger.info("{}", config);
        logger.info("{}", CLIENT_CONFIG.getConnectorProvider());
        logger.info("{}", config.getConnectorProvider());
        return client;
    }

}

/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.bio;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ls.demon.netkit.bio.handler.HttpHandler;

/**
 * 
 * @author lisong
 * @version $Id: BioServer.java, v 0.1 2017年7月29日 上午9:46:22 lisong Exp $
 */
public class BioServer {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(BioServer.class);

    private int                 port   = 8888;
    private boolean             mult   = false;
    private ServerSocket        ss;

    protected Executor          exe    = Executors.newFixedThreadPool(5);

    /**
     * 
     */
    public BioServer() {
        super();
    }

    /**
     * @param port
     */
    public BioServer(int port) {
        super();
        this.port = port;
    }

    /**
     * @param port
     * @param mult
     */
    public BioServer(int port, boolean mult) {
        super();
        this.port = port;
        this.mult = mult;
    }

    public void start() {
        try {
            ss = new ServerSocket(port);
            logger.info("listening {} .....", port);
            for (;;) {
                Socket s = ss.accept();
                logger.info("new connection... {}", s);

                if (mult) {
                    exe.execute(new HttpHandler(s));
                } else {
                    new HttpHandler(s).run();
                }
            }

        } catch (Exception e) {
            logger.error("", e);
        } finally {
            IOUtils.closeQuietly(ss);
        }
    }

    public static void main(String[] args) {
        new BioServer(8888).start();
    }

}

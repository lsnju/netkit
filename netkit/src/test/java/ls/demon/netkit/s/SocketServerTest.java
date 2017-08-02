/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.s;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author song.li@witontek.com
 * @version $Id: SocketServerTest.java, v 0.1 2017年7月28日 下午4:58:20 song.li@witontek.com Exp $
 */
public class SocketServerTest {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(SocketServerTest.class);

    public static void main(String[] args) {
        logger.info("");
        Executor ee = Executors.newFixedThreadPool(5);
        ServerSocket ss = null;
        try {

            ss = new ServerSocket(8888);
            logger.info("server started on port 8888");
            for (;;) {
                Socket s = ss.accept();
                logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                logger.info("new conn {}", s);
                // ee.execute(new SocketTask(s));
                new SocketTask(s).run();
            }

        } catch (Exception e) {
            logger.error("1111", e);
        } finally {
            IOUtils.closeQuietly(ss);
            logger.info("ssssssss-end");
        }
    }

    static class SocketTask implements Runnable {

        private Socket s;

        /**
         * @param s
         */
        public SocketTask(Socket s) {
            super();
            this.s = s;
        }

        /** 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(s.getInputStream(), "utf-8"));

                BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream(), "utf-8"));

                for (;;) {
                    String line = br.readLine();
                    if (StringUtils.isBlank(line)) {
                        logger.info("return ok .................................");
                        bw.write("HTTP/1.0 200 Connection Established\r\n\r\n");
                        bw.flush();
                        // break;
                    }
                    logger.info(line);
                }
            } catch (Exception e) {
                logger.error("2222", e);
            } finally {
                IOUtils.closeQuietly(s);
                logger.info("end");
            }
        }

    }
}

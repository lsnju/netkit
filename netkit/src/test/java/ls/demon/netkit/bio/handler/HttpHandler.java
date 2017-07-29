/**
 * Witontek.com.
 * Copyright (c) 2012-2017 All Rights Reserved.
 */
package ls.demon.netkit.bio.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author lisong
 * @version $Id: HttpHandler.java, v 0.1 2017年7月29日 上午10:37:58 lisong Exp $
 */
public class HttpHandler implements Runnable {
    /**
    * Logger for this class
    */
    private static final Logger logger  = LoggerFactory.getLogger(HttpHandler.class);

    public static final String  CONNECT = "HTTP/1.1 200 Connection Established\r\n\r\n";

    private Socket              s;

    public HttpHandler(Socket s) {
        super();
        this.s = s;
    }

    /** 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(s.getOutputStream()))) {
            for (;;) {

                String line = br.readLine();
                if (line == null) {
                    logger.info("< close.....");
                    break;
                }
                logger.info("< {}", line);

                if (StringUtils.isBlank(line)) {
                    logger.info("> {}", CONNECT);
                    bw.write(CONNECT);
                    bw.flush();
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            IOUtils.closeQuietly(s);
        }
    }

}

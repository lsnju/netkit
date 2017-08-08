package ls.demon.netkit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 *
 */
public class App {
    /**
    * Logger for this class
    */
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - start"); //$NON-NLS-1$
        }

        System.out.println("Hello World!");
        logger.info("{}", App.class.getResource(""));
        logger.info("{}", App.class.getResource("/"));
        logger.info("xxxxxxxxxx");
        logger.info("{}", String.class.getResource("/"));
        logger.info("{}", StringUtils.class.getResource("/"));
        logger.info("xxxxxxxxxx");
        logger.info("{}", Thread.currentThread().getContextClassLoader().getResource(""));

        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - end"); //$NON-NLS-1$
        }
    }
}

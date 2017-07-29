package ls.demon.netkit;

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

        if (logger.isDebugEnabled()) {
            logger.debug("main(String[]) - end"); //$NON-NLS-1$
        }
    }
}

package logger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionalBlockLogger {
    public Logger logger;
	
	public FunctionalBlockLogger(Class<?> className) {
	    logger = LogManager.getLogger(className);
	}
	
    public void debug(Object message) {
    	logger.debug(message);
    }
    
    public void info(Object message) {
    	logger.info(message);
    }
    
    public void error(Object message) {
    	logger.error(message);
    }
}

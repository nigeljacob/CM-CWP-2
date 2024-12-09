import java.util.logging.Logger;

public class Log {
    private static final Logger logger = Logger.getLogger(Log.class.getName());


    public void writeLog(String message, String type) {
        switch (type) {
            case "INFO":
                logger.info(message);
                break;
            case "WARNING":
                logger.warning(message);
                break;
            case "ERROR":
                logger.severe(message);
                break;
        }
    }
}

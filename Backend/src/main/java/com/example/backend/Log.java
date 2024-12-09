package com.example.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Log {
    private static final Logger logger = LoggerFactory.getLogger(Log.class);


    public void writeLog(String message, String type) {
        switch (type) {
            case "INFO":
                logger.info(message);
                break;
            case "WARNING":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "FATAL":
                logger.error(message);
                break;
        }
    }
}

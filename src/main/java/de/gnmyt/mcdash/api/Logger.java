package de.gnmyt.mcdash.api;

import org.apache.logging.log4j.LogManager;

public class Logger {

    private final org.apache.logging.log4j.Logger logger;

    /**
     * Creates a new Logger
     *
     * @param clazz The class you want to use the logger for
     */
    public Logger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }

    /**
     * Logs an info message
     *
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    /**
     * Logs an info message
     *
     * @param clazz   The logger class
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public static void info(Class<?> clazz, String message, Object... args) {
        LogManager.getLogger(clazz).info(message, args);
    }

    /**
     * Logs a warning message
     *
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    /**
     * Logs a warning message
     *
     * @param clazz   The logger class
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public static void warn(Class<?> clazz, String message, Object... args) {
        LogManager.getLogger(clazz).warn(message, args);
    }

    /**
     * Logs an error message
     *
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    /**
     * Logs an error message
     *
     * @param clazz   The logger class
     * @param message The message you want to log
     * @param args    The arguments you want to use in the message
     */
    public static void error(Class<?> clazz, String message, Object... args) {
        LogManager.getLogger(clazz).error(message, args);
    }

}

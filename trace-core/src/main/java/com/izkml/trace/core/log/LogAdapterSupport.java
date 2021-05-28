package com.izkml.trace.core.log;

/**
 * 日志适配帮助类
 */
public final class LogAdapterSupport {

    private static final String LOG4J_SPI = "org.apache.logging.log4j.spi.ExtendedLogger";

    private static final String LOG4J_SLF4J_PROVIDER = "org.apache.logging.slf4j.SLF4JProvider";

    private static final String SLF4J_SPI = "org.slf4j.spi.LocationAwareLogger";

    private static final String SLF4J_API = "org.slf4j.Logger";

    private static final LogType logType;

    enum LogType {SLF4J,LOG4J,NULL}

    static {
        if (isPresent(LOG4J_SPI)) {
            if (isPresent(LOG4J_SLF4J_PROVIDER) && isPresent(SLF4J_SPI)) {
                // log4j-to-slf4j bridge -> we'll rather go with the SLF4J SPI;
                // however, we still prefer Log4j over the plain SLF4J API since
                // the latter does not have location awareness support.
                logType = LogType.SLF4J;
            }
            else {
                // Use Log4j 2.x directly, including location awareness support
                logType = LogType.LOG4J;
            }
        }
        else if (isPresent(SLF4J_SPI) || isPresent(SLF4J_API)) {
            // Full SLF4J SPI including location awareness support
            logType = LogType.SLF4J;
        }else{
            logType = LogType.NULL;
        }
    }

    private static boolean isPresent(String className) {
        try {
            Class.forName(className, false, DefaultLogPrinter.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static LogType getLogType(){
        return logType;
    }
}

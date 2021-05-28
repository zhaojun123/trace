package com.izkml.trace.core.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志适配，支持slf4j和log4j
 */
public class DefaultLogPrinter implements LogPrinter {

    private static final ConcurrentHashMap<String,Log> logCache = new ConcurrentHashMap<>();


    @Override
    public void log(String message, String level,String caller, Throwable e) {
        if(level!=null && caller !=null){
            level = level.toUpperCase();
            Log log = createLog(caller);
            if(e!=null){
                logThrowable(log,message,level,e);
            }else{
                logNullThrowable(log,message,level);
            }
        }
    }

    private void logThrowable(Log log,String message, String level,Throwable e){
        switch (level){
            case "FATAL":
                log.fatal(message,e);
                break;
            case "ERROR":
                log.error(message,e);
                break;
            case "WARN":
                log.warn(message,e);
                break;
            case "INFO":
                log.info(message,e);
                break;
            case "DEBUG":
                log.debug(message,e);
                break;
            case "TRACE":
                log.trace(message,e);
                break;
            default:
        }
    }

    private void logNullThrowable(Log log,String message, String level){
        switch (level){
            case "FATAL":
                log.fatal(message);
                break;
            case "ERROR":
                log.error(message);
                break;
            case "WARN":
                log.warn(message);
                break;
            case "INFO":
                log.info(message);
                break;
            case "DEBUG":
                log.debug(message);
                break;
            case "TRACE":
                log.trace(message);
                break;
            default:
        }
    }

    private Log createLog(String caller){
        Log log = logCache.get(caller);
        if(log == null){
            switch (LogAdapterSupport.getLogType()){
                case SLF4J:
                    log = new Slf4jLog(caller);
                    break;
                case LOG4J:
                    log = new Log4jLog(caller);
                    break;
                case NULL:
                    log = new NullLog();
                    break;
                default:
                    log = new NullLog();
            }
            logCache.put(caller,log);
        }
        return log;
    }


    private static class NullLog implements Log{

        @Override
        public boolean isFatalEnabled() {
            return false;
        }

        @Override
        public boolean isErrorEnabled() {
            return false;
        }

        @Override
        public boolean isWarnEnabled() {
            return false;
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void fatal(Object message) {

        }

        @Override
        public void fatal(Object message, Throwable t) {

        }

        @Override
        public void error(Object message) {

        }

        @Override
        public void error(Object message, Throwable t) {

        }

        @Override
        public void warn(Object message) {

        }

        @Override
        public void warn(Object message, Throwable t) {

        }

        @Override
        public void info(Object message) {

        }

        @Override
        public void info(Object message, Throwable t) {

        }

        @Override
        public void debug(Object message) {

        }

        @Override
        public void debug(Object message, Throwable t) {

        }

        @Override
        public void trace(Object message) {

        }

        @Override
        public void trace(Object message, Throwable t) {

        }
    }

    private static class Log4jLog implements Log{

        private final ExtendedLogger logger;

        private static final String FQCN = Log4jLog.class.getName();

        private static final LoggerContext loggerContext =
                LogManager.getContext(Log4jLog.class.getClassLoader(), false);

        public Log4jLog(String name) {
            this.logger = loggerContext.getLogger(name);
        }

        @Override
        public boolean isFatalEnabled() {
            return this.logger.isEnabled(Level.FATAL);
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isEnabled(Level.ERROR);
        }

        @Override
        public boolean isWarnEnabled() {
            return this.logger.isEnabled(Level.WARN);
        }

        @Override
        public boolean isInfoEnabled() {
            return this.logger.isEnabled(Level.INFO);
        }

        @Override
        public boolean isDebugEnabled() {
            return this.logger.isEnabled(Level.DEBUG);
        }

        @Override
        public boolean isTraceEnabled() {
            return this.logger.isEnabled(Level.TRACE);
        }

        @Override
        public void fatal(Object message) {
            log(Level.FATAL, message, null);
        }

        @Override
        public void fatal(Object message, Throwable exception) {
            log(Level.FATAL, message, exception);
        }

        @Override
        public void error(Object message) {
            log(Level.ERROR, message, null);
        }

        @Override
        public void error(Object message, Throwable exception) {
            log(Level.ERROR, message, exception);
        }

        @Override
        public void warn(Object message) {
            log(Level.WARN, message, null);
        }

        @Override
        public void warn(Object message, Throwable exception) {
            log(Level.WARN, message, exception);
        }

        @Override
        public void info(Object message) {
            log(Level.INFO, message, null);
        }

        @Override
        public void info(Object message, Throwable exception) {
            log(Level.INFO, message, exception);
        }

        @Override
        public void debug(Object message) {
            log(Level.DEBUG, message, null);
        }

        @Override
        public void debug(Object message, Throwable exception) {
            log(Level.DEBUG, message, exception);
        }

        @Override
        public void trace(Object message) {
            log(Level.TRACE, message, null);
        }

        @Override
        public void trace(Object message, Throwable exception) {
            log(Level.TRACE, message, exception);
        }

        private void log(Level level, Object message, Throwable exception) {
            if (message instanceof String) {
                // Explicitly pass a String argument, avoiding Log4j's argument expansion
                // for message objects in case of "{}" sequences (SPR-16226)
                if (exception != null) {
                    this.logger.logIfEnabled(FQCN, level, null, (String) message, exception);
                }
                else {
                    this.logger.logIfEnabled(FQCN, level, null, (String) message);
                }
            }
            else {
                this.logger.logIfEnabled(FQCN, level, null, message, exception);
            }
        }
    }

    private static class Slf4jLog implements Log{

        private final Logger logger;

        public Slf4jLog(String className){
            logger = LoggerFactory.getLogger(className);
        }

        @Override
        public boolean isFatalEnabled() {
            return isErrorEnabled();
        }

        @Override
        public boolean isErrorEnabled() {
            return this.logger.isErrorEnabled();
        }

        @Override
        public boolean isWarnEnabled() {
            return this.logger.isWarnEnabled();
        }

        @Override
        public boolean isInfoEnabled() {
            return this.logger.isInfoEnabled();
        }

        @Override
        public boolean isDebugEnabled() {
            return this.logger.isDebugEnabled();
        }

        @Override
        public boolean isTraceEnabled() {
            return this.logger.isTraceEnabled();
        }

        @Override
        public void fatal(Object message) {
            error(message);
        }

        @Override
        public void fatal(Object message, Throwable exception) {
            error(message, exception);
        }

        @Override
        public void error(Object message) {
            if (message instanceof String || this.logger.isErrorEnabled()) {
                this.logger.error(String.valueOf(message));
            }
        }

        @Override
        public void error(Object message, Throwable exception) {
            if (message instanceof String || this.logger.isErrorEnabled()) {
                this.logger.error(String.valueOf(message), exception);
            }
        }

        @Override
        public void warn(Object message) {
            if (message instanceof String || this.logger.isWarnEnabled()) {
                this.logger.warn(String.valueOf(message));
            }
        }

        @Override
        public void warn(Object message, Throwable exception) {
            if (message instanceof String || this.logger.isWarnEnabled()) {
                this.logger.warn(String.valueOf(message), exception);
            }
        }

        @Override
        public void info(Object message) {
            if (message instanceof String || this.logger.isInfoEnabled()) {
                this.logger.info(String.valueOf(message));
            }
        }

        @Override
        public void info(Object message, Throwable exception) {
            if (message instanceof String || this.logger.isInfoEnabled()) {
                this.logger.info(String.valueOf(message), exception);
            }
        }

        @Override
        public void debug(Object message) {
            if (message instanceof String || this.logger.isDebugEnabled()) {
                this.logger.debug(String.valueOf(message));
            }
        }

        @Override
        public void debug(Object message, Throwable exception) {
            if (message instanceof String || this.logger.isDebugEnabled()) {
                this.logger.debug(String.valueOf(message), exception);
            }
        }

        @Override
        public void trace(Object message) {
            if (message instanceof String || this.logger.isTraceEnabled()) {
                this.logger.trace(String.valueOf(message));
            }
        }

        @Override
        public void trace(Object message, Throwable exception) {
            if (message instanceof String || this.logger.isTraceEnabled()) {
                this.logger.trace(String.valueOf(message), exception);
            }
        }
    }

    /**
     * 通用日志接口
     */
    public interface Log{

        boolean isFatalEnabled();

        boolean isErrorEnabled();

        boolean isWarnEnabled();

        boolean isInfoEnabled();

        boolean isDebugEnabled();

        boolean isTraceEnabled();

        void fatal(Object message);

        void fatal(Object message, Throwable t);

        void error(Object message);

        void error(Object message, Throwable t);

        void warn(Object message);

        void warn(Object message, Throwable t);


        void info(Object message);


        void info(Object message, Throwable t);


        void debug(Object message);


        void debug(Object message, Throwable t);


        void trace(Object message);


        void trace(Object message, Throwable t);

    }

}

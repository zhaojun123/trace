package com.izkml.trace.core;

import com.izkml.trace.core.log.DefaultLogPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TraceContext工厂
 */
public class TraceContextFactory {

    private static TraceContext traceContext;

    private TraceContextFactory(){}

    public static TraceContext getContext(){
        if(traceContext == null){
            throw new IllegalStateException("traceContext is null please invoking createContext() to get");
        }
        return traceContext;
    }

    public static synchronized TraceContext createContext(TraceConfiguration configuration){
        if(traceContext!=null){
            throw new IllegalStateException("traceContext already exist please invoking getContext() to get");
        }
        TraceContext traceContext = new TraceContext(configuration);
        traceContext.init();
        TraceContextFactory.traceContext = traceContext;
        return traceContext;
    }
}

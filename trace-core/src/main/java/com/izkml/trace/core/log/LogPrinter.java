package com.izkml.trace.core.log;

/**
 * 日志打印适配
 */
public interface LogPrinter {

    /**
     * 通用日志打印
     * @param message
     * @param level
     * @param caller
     * @param e
     */
    void log(String message,String level,String caller,Throwable e);

}

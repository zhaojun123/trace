package com.izkml.trace.core.log;


import com.izkml.trace.core.TraceContext;

/**
 * 日志配置
 */
public interface LogConfig {

    /**
     * 初始化
     * @param traceContext
     */
    void init(TraceContext traceContext);

}

package com.izkml.trace.core;

/**
 * 常量类
 * 所有的参数配置都要以trace.开始，例如
 * trace.logging.trace.level = INFO
 */
public class TraceConstant {

    /**
     * 是否自动刷新配置参数，默认true
     */
    public static final String PROPERTIES_AUTO_REFRESH = "properties.auto.refresh";

    /**
     * trace日志打印级别，默认info
     */
    public static final String LOGGING_TRACE_LEVEL = "logging.trace.level";

    /**
     * 项目名称
     */
    public static final String APPLICATION_NAME = "application.name";

    /**
     * 追踪信息详细程度 参考{@link InfoTypeHandler}
     */
    public static final String INFO_TYPE = "info.type";

    public static final String SERVLET_SPAN_NAME = "servlet";

    public static final String HTTP_CLIENT_SPAN_NAME = "http-client";

    public static final String MYBATIS_SPAN_NAME = "mybatis";

    public static final String RABBIT_SEND_SPAN_NAME = "rabbit-send";

    public static final String RABBIT_RECEIVE_SPAN_NAME = "rabbit-receive";

    public static final String FEIGN_SPAN_NAME = "feign";

    /**
     * 各节点传递trace用到的属性名称
     */
    public static final String ZKML_APPLICATION_NAME = "ZKML-APPLICATION-NAME";
    public static final String ZKML_TRACE_ID = "ZKML-TRACE-ID";
    public static final String ZKML_PARENT_SPAN_ID = "ZKML-PARENT-SPAN-ID";
    public static final String ZKML_SPAN_ID = "ZKML-SPAN-ID";
    public static final String ZKML_TRACE = "ZKML_TRACE";
    public static final String ZKML_SPAN_NAME = "ZKML-SPAN-NAME";
    public static final String ZKML_FINISH_TIME = "ZKML-FINISH-TIME";
    public static final String ZKML_SPAN_TYPE = "ZKML-SPAN-TYPE";

}

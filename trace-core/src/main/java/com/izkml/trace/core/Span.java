package com.izkml.trace.core;

import com.izkml.trace.core.util.HexCodec;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Span {

    public static final String TOP_TRACE_ID = "0";

    /**
     * 节点默认名称
     */
    public static final String NORMAL_SPAN_NAME = "NORMAL";

    /**
     * 一个请求链路唯一id
     */
    private String traceId;

    /**
     * 上游节点id
     */
    private String parentSpanId;

    /**
     * 本节点id
     */
    private String spanId;

    /**
     * 本节点名称
     */
    private String spanName = NORMAL_SPAN_NAME;

    /**
     * 该节点完成花费的时间
     */
    private Long finishTime;

    /**
     * 该节点开始时间
     */
    private Long startTime;

    /**
     * 自定义标签，用于描述
     */
    private Object tags;

    /**
     * 节点类型
     */
    private SpanType spanType = SpanType.USER;

    /**
     * 报错信息
     */
    private Throwable throwable;

    /**
     * 业务标示，例如servlet的 url、mybatis的MappedStatementId 等
     */
    private String businessMark;

    public String getBusinessMark() {
        return businessMark;
    }

    public void setBusinessMark(String businessMark) {
        this.businessMark = businessMark;
    }

    public InfoTypeHandler infoTypeHandler;

    public InfoTypeHandler getInfoTypeHandler() {
        return infoTypeHandler;
    }

    public void setInfoTypeHandler(InfoTypeHandler infoTypeHandler) {
        this.infoTypeHandler = infoTypeHandler;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public SpanType getSpanType() {
        return spanType;
    }

    public void setSpanType(SpanType spanType) {
        this.spanType = spanType;
    }

    /**
     * 应用名称
     */
    private String applicationName;

    public String getSpanName() {
        return spanName;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public String getSpanId() {
        return spanId;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Object getTags() {
        return tags;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public void setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public void setSpanName(String spanName) {
        this.spanName = spanName;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setTags(Object tags) {
        this.tags = tags;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 完成该链路
     * @return
     */
    public long finish(){
        if(startTime!=null){
            this.finishTime = System.currentTimeMillis() - startTime;
        }
        return getFinishTime();
    }

    /**
     * 通过父Span 创建 子 Span
     * @param spanName
     * @return
     */
    public Span createNextSpan(String spanName){
        return createByParent(spanName,null);
    }


    public Span createByParent(String spanName,Map<String, Object> tags){
        return new Builder()
                .traceId(this.traceId)
                .parentSpanId(this.spanId)
                .spanId(createSpanId())
                .spanName(spanName)
                .startTime(System.currentTimeMillis())
                .applicationName(this.applicationName)
                .tags(tags)
                .spanType(this.spanType)
                .build();
    }

    public boolean sameTrace(Span other){
        return this.traceId.equals(other.getTraceId());
    }

    /**
     * 生成spanId
     * @return
     */
    public static String createSpanId(){
        long idSeed = ThreadLocalRandom.current().nextLong();
        return HexCodec.toLowerHex(idSeed);
    }

    public static Span create(String applicationName,String spanName){
        return create(applicationName,spanName,null);
    }

    public static Span create(String applicationName,String spanName,Map<String, Object> tags){
        String spanId = createSpanId();
        return new Builder()
                .applicationName(applicationName)
                .traceId(spanId)
                .parentSpanId(TOP_TRACE_ID)
                .spanId(spanId)
                .spanName(spanName)
                .startTime(System.currentTimeMillis())
                .tags(tags)
                .build();

    }


    public static class Builder{
        private String traceId;
        private String parentSpanId;
        private String spanId;
        private Long startTime;
        private Long finishTime;
        private String spanName;
        private String applicationName;
        private Object tags;
        private SpanType spanType = SpanType.USER;

        public Builder traceId(String traceId){
            this.traceId = traceId;
            return this;
        }

        public Builder parentSpanId(String parentSpanId){
            this.parentSpanId = parentSpanId;
            return this;
        }

        public Builder spanId(String spanId){
            this.spanId = spanId;
            return this;
        }

        public Builder spanName(String spanName){
            this.spanName = spanName;
            return this;
        }

        public Builder startTime(long startTime){
            this.startTime = startTime;
            return this;
        }

        public Builder finishTime(long finishTime){
            this.finishTime = finishTime;
            return this;
        }

        public Builder applicationName(String applicationName){
            this.applicationName = applicationName;
            return this;
        }

        public Builder tags(Object tags){
            this.tags = tags;
            return this;
        }

        public Builder spanType(SpanType spanType){
            this.spanType = spanType;
            return this;
        }

        public Span build(){
            Span span = new Span();
            span.traceId = this.traceId;
            span.parentSpanId = this.parentSpanId;
            span.spanId = this.spanId == null?createSpanId():this.spanId;
            span.spanName = this.spanName == null?NORMAL_SPAN_NAME:this.spanName;
            span.startTime = this.startTime == null?System.currentTimeMillis():this.startTime;
            span.finishTime = this.finishTime;
            span.applicationName = this.applicationName;
            span.tags = this.tags;
            span.spanType = this.spanType;
            return span;
        }
    }

    public enum SpanType{
        SYSTEM,USER
    }
}

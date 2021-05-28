package com.izkml.trace.core;

/**
 * Span装饰器
 */
public interface SpanDecorator {

    /**
     * 对span进行装饰处理
     * @param span
     */
    void decorate(Span span);

    /**
     * 当追踪完成时的操作
     * @param span
     */
    void finish(Span span);

}

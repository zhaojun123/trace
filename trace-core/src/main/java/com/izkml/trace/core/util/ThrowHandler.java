package com.izkml.trace.core.util;

/**
 * 处理lambda 表达式中无法抛出Exception的问题
 */
public class ThrowHandler {

    public static <E extends Exception> void doThrow(Exception e) throws E {
        throw (E)e;
    }

}

package com.izkml.trace.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

/**
 * 节点当前上下文
 */
public class CurrentSpanContext {

    private static ThreadLocal<SpanContextList<Span>> threadLocal =  new InheritableThreadLocal<SpanContextList<Span>>(){

        private String name = "current-span-thread-local";

        public String getName() {
            return name;
        }

        @Override
        public SpanContextList<Span> initialValue(){
            return new SpanContextList<Span>();
        }

    };

    /**
     * 获取当前节点的span
     * @return
     */
    public static Span getCurrentSpan(){
        List<Span> spanList = threadLocal.get();
        if(spanList.isEmpty()){
            return null;
        }
        return spanList.get(spanList.size()-1);
    }

    /**
     * 将span添加到上下文当中
     * @param span
     */
    public static void addSpan(Span span){
        List<Span> spanList = threadLocal.get();
        spanList.add(span);
    }

    /**
     * 当一个span完成时调用，计算span的耗时
     * @param span
     */
    public static void finish(Span span){
        ListIterator<Span> listIterator = threadLocal.get().listIterator();
        while(listIterator.hasNext()){
            Span current = listIterator.next();
            if(Objects.equals(current.getSpanId(),span.getSpanId())){
                current.finish();
                current.setSpanType(Span.SpanType.SYSTEM);
                break;
            }
        }

    }

    /**
     * 将span从上下文中移除
     * @param span
     */
    public static void remove(Span span){
        ListIterator<Span> listIterator = threadLocal.get().listIterator();
        while(listIterator.hasNext()){
            Span current = listIterator.next();
            if(Objects.equals(current.getSpanId(),span.getSpanId())){
                listIterator.remove();
                //因为tomcat连接池的缘故，在这里清除上一次线程遗留下来的throwableSpan
                Span throwableSpan = getThrowableSpan();
                if(throwableSpan!=null && !current.sameTrace(throwableSpan)){
                    removeThrowable();
                }
                break;
            }
        }
    }

    public static void setThrowable(Throwable e){
        if(e == null){
            return;
        }
        Span current = getCurrentSpan();
        if(current == null){
            return;
        }
        Span throwableSpan = getThrowableSpan();
        if(throwableSpan == null || e!=throwableSpan.getThrowable()){
            current.setThrowable(e);
            threadLocal.get().setThrowableSpan(current);
        }
    }

    public static void removeThrowable(){
        threadLocal.get().setThrowableSpan(null);
    }

    public static Span getThrowableSpan(){
        return threadLocal.get().getThrowableSpan();
    }

    private static class SpanContextList<T> extends ArrayList<T>{

        private Span throwableSpan;

        public Span getThrowableSpan() {
            return throwableSpan;
        }

        public void setThrowableSpan(Span throwableSpan) {
            this.throwableSpan = throwableSpan;
        }
    }
}

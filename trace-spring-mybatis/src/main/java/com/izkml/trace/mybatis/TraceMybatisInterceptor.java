package com.izkml.trace.mybatis;

import com.izkml.trace.core.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

import static com.izkml.trace.core.TraceConstant.MYBATIS_SPAN_NAME;

@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class})
        ,@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
        ,@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class TraceMybatisInterceptor implements Interceptor {

    private SpanHandler spanHandler;

    private TraceContext traceContext = TraceContextFactory.getContext();

    public TraceMybatisInterceptor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object params = invocation.getArgs()[1];
        Span span = traceContext.nextSpan(MYBATIS_SPAN_NAME);
        span.setBusinessMark(ms.getId());
        spanHandler.handle(span,TraceMybatisInterceptor.class);
        try{
            return invocation.proceed();
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        }finally {
            span.setInfoTypeHandler(new MybatisInfoTypeHandler(ms,params));
            spanHandler.finish(span,TraceMybatisInterceptor.class);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}

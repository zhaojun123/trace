package com.izkml.trace.rabbitmq;

import com.izkml.trace.core.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import static com.izkml.trace.core.TraceConstant.RABBIT_SEND_SPAN_NAME;

/**
 * RabbitTemplate拦截器
 */
public class TraceRabbitInterceptor implements MethodInterceptor {

    private static final String EXCHANGE_PARAMETER_NAME = "exchange";

    private static final String ROUTING_KEY_PARAMETER_NAME = "routingKey";

    /**
     * 需要拦截的方法
     */
    private static String[] interceptMethods = new String[]{"send","convertAndSend","sendAndReceive","convertSendAndReceive","execute"};

    /**
     * 存储需要拦截的方法相对应的参数名称
     */
    private static Map<Method,String[]> parameterNameMap = new HashMap<>();

    static {
        DefaultParameterNameDiscoverer defaultParameterNameDiscoverer = new DefaultParameterNameDiscoverer();
        for(Method method:RabbitTemplate.class.getMethods()){
            if(canIntercept(method)){
                String[] parameterNames = defaultParameterNameDiscoverer.getParameterNames(method);
                if(parameterNames!=null && parameterNames.length>0){
                    parameterNameMap.put(method,parameterNames);
                }
            }
        }
    }

    private SpanHandler spanHandler;

    private TraceContext traceContext = TraceContextFactory.getContext();

    public TraceRabbitInterceptor(SpanHandler spanHandler){
        this.spanHandler = spanHandler;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        //如果不是public方法，直接过
        if(!Modifier.isPublic(method.getModifiers())){
            return invocation.proceed();
        }
        if(!canIntercept(method)){
            return invocation.proceed();
        }
        Span span = traceContext.nextSpan(RABBIT_SEND_SPAN_NAME);
        String exchange = (String)getParameterValue(method,EXCHANGE_PARAMETER_NAME,arguments);
        String routingKey = (String)getParameterValue(method,ROUTING_KEY_PARAMETER_NAME,arguments);
        if(exchange!=null && routingKey!=null){
            span.setBusinessMark(exchange+"/"+routingKey);
        }
        spanHandler.handle(span, TraceRabbitInterceptor.class);
        try{
            return invocation.proceed();
        }catch (Throwable e){
            //记录错误信息
            CurrentSpanContext.setThrowable(e);
            throw  e;
        }finally {
            spanHandler.finish(span, TraceRabbitInterceptor.class);
        }
    }

    /**
     * 是否需要拦截
     * @param method
     * @return
     */
    private static boolean canIntercept(Method method){
        for(String interceptMethod: interceptMethods){
            if(interceptMethod.equals(method.getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取参数名称对应的参数值位置
     * @param method
     * @param name
     * @param arguments
     * @return
     */
    private static Object getParameterValue(Method method,String name,Object[] arguments){
        if(arguments == null || arguments.length == 0){
            return null;
        }
        String[] parameterNames = parameterNameMap.get(method);
        if(parameterNames == null){
            return null;
        }
        if(parameterNames.length != arguments.length){
            return null;
        }
        for(int i=0;i<parameterNames.length;i++){
            if(parameterNames[i].equals(name)){
                return arguments[i];
            }
        }
        return null;
    }
}

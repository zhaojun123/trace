package com.izkml.trace.core;

/**
 * 根据 trace.[spanName].info.type 来确定记录信息的详细程度
 * 例如 trace.servlet.info.type = normal
 * 也可以针对具体的某条业务链路进行配置 trace.[spanName].info.type.[businessMark]
 * 例如 trace.servlet.info.type.user/list = normal
 * @param <T>
 */
public interface InfoTypeHandler<T> {

    /**
     * 不处理追踪信息
     * @return
     */
    default T close(){
        return null;
    }

    /**
     * 记录基本的业务信息和追踪信息
     * @return
     */
    T simple();

    /**
     * 记录正常的业务信息和追踪信息
     * @return
     */
    T normal();

    /**
     * 记录详细的业务信息和追踪信息
     * @return
     */
    T full();


    default T handler(String infoType){
        if(infoType == null){
            return normal();
        }
        switch (infoType.toUpperCase()){
            case "CLOSE":
                return close();
            case "SIMPLE":
                return simple();
            case "NORMAL":
                return normal();
            case "FULL":
                return full();
            default:
                return normal();
        }
    }

}

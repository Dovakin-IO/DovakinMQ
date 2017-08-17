package io.dovakinmq.validator;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class ValidatorCache {
    public ConcurrentHashMap<String, Method> methodHashMap
            = new ConcurrentHashMap<String, Method>();

    public void add(String nodeName, Method method){
        methodHashMap.putIfAbsent(nodeName,method);
    }

    public Method get(String nodeName){
        return methodHashMap.get(nodeName);
    }
}

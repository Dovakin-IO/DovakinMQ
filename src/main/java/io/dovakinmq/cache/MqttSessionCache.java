package io.dovakinmq.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class MqttSessionCache {
    public final static ConcurrentHashMap<String, MqttSession> cache;

    static{
        cache = new ConcurrentHashMap<>();
    }

    public static void put(String clientId, MqttSession mqttSession){
        cache.putIfAbsent(clientId, mqttSession);
    }

    public static MqttSession get(String clientId){
        return cache.get(clientId);
    }

    public static void clean(String clientId){
        cache.remove(clientId);
    }


    public boolean isExist(String clientId){
        return get(clientId) == null ? false : true;
    }
}

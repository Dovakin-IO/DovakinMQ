package io.dovakinmq.validator;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class ValidatorMap {
    private final static ConcurrentHashMap<MqttMessageType, ValidatorCache> validatorMap
            = new ConcurrentHashMap<MqttMessageType, ValidatorCache>();

    public static void add(MqttMessageType type, ValidatorCache cache){
        validatorMap.putIfAbsent(type, cache);
    }

    public static ValidatorCache get(MqttMessageType type){
        return validatorMap.get(type);
    }
}

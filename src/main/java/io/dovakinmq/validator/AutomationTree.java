package io.dovakinmq.validator;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public abstract class AutomationTree {

    protected ConcurrentHashMap<String, Method> childNodes;

    public ValidateResult validate(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder){

        return run(mqttMessage,channel,recorder);
    }

    protected abstract ValidateResult run(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder);
}

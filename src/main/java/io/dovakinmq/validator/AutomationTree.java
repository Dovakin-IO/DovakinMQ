package io.dovakinmq.validator;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.HashMap;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public abstract class AutomationTree {

    protected HashMap<String, MqttValidator> childNodes;

    public boolean validate(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder){

        return run(mqttMessage,channel,recorder);
    }

    protected abstract boolean run(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder);
}

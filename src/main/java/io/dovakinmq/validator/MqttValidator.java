package io.dovakinmq.validator;

import io.dovakinmq.cache.RequestRecorder;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public interface MqttValidator {

    void init(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder);
}

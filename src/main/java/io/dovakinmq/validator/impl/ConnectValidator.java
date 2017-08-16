package io.dovakinmq.validator.impl;

import io.dovakinmq.validator.AutomationTree;
import io.dovakinmq.validator.MqttValidator;
import io.dovakinmq.validator.RequestRecorder;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public class ConnectValidator extends AutomationTree implements MqttValidator {


    protected boolean run(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder) {
        return false;
    }
}

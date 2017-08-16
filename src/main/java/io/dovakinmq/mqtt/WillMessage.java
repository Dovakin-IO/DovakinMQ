package io.dovakinmq.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class WillMessage {
    private final Topic topic;
    private final Payload payload;
    private final boolean isRetain;
    private final MqttQoS qos;

    public WillMessage(Topic topic, Payload payload, boolean isRetain, MqttQoS qos){
        this.topic = topic;
        this.payload = payload;
        this.isRetain = isRetain;
        this.qos = qos;
    }

    public Topic getTopic() {
        return topic;
    }

    public Payload getPayload() {
        return payload;
    }

    public boolean isRetain() {
        return isRetain;
    }

    public MqttQoS getQos() {
        return qos;
    }
}

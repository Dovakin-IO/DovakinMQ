package io.dovakinmq.mqtt.builder;

import io.netty.handler.codec.mqtt.*;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class MqttMessageBuilder {

    public static MqttConnAckMessage buildConnAckMessage(MqttConnectReturnCode returnCode,
                                                         MqttQoS qos, Boolean sessionPresent){
        MqttFixedHeader fixedHeader
                = new MqttFixedHeader(MqttMessageType.CONNACK,
                false,
                qos,
                false,
                0);
        MqttConnAckVariableHeader connAckVariableHeader
                = new MqttConnAckVariableHeader(returnCode,
                sessionPresent);
        MqttConnAckMessage connAckMessage = new MqttConnAckMessage(fixedHeader, connAckVariableHeader);
        return connAckMessage;
    }
}

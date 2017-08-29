package io.dovakinmq.mqtt.builder;

import io.netty.handler.codec.mqtt.*;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class MqttMessageBuilder {

    private static MqttMessage pingRespMessage;

    public static MqttMessage buildPingRespMessage(){
        if(pingRespMessage != null) return pingRespMessage;
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP,
                false,
                MqttQoS.AT_LEAST_ONCE,
                false,
                0);
        pingRespMessage = new MqttMessage(fixedHeader);
        return pingRespMessage;
    }

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

package io.dovakinmq.processor.impl;

import io.dovakinmq.constant.DovakinConstants;
import io.dovakinmq.manager.ConnectionStore;
import io.dovakinmq.manager.MqttConnection;
import io.dovakinmq.mqtt.builder.MqttMessageBuilder;
import io.dovakinmq.processor.Processor;
import io.dovakinmq.server.MQServer;
import io.dovakinmq.validator.RequestRecorder;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;

/**
 * Created by Link on 2017/8/15.
 */
public class MqttProcessor implements Processor<MqttMessage> {

    private MQServer server;

    public MqttProcessor(MQServer server){
        this.server = server;
    }

    public void process(MqttMessage mqttMessage, Channel ch){
        MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
        Attribute<RequestRecorder> attribute =
                ch.attr(DovakinConstants.RECORDER_ATTRIBUTE_KEY);
        RequestRecorder requestRecorder = attribute.get();
        requestRecorder.record(mqttMessageType);
        switch (mqttMessageType){
            case CONNECT:
                connect((MqttConnectMessage)mqttMessage, ch, requestRecorder);
                break;
        }
    }

    private void connect(MqttConnectMessage mqttConnectMessage, Channel ch, RequestRecorder recorder){

        //MQTT_3.1.0-1
        if(recorder.getHistory(0) != MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.0-2
        if(recorder.getHistory(1) == null
                || recorder.getHistory(1) == MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.2-1
        if(!mqttConnectMessage.variableHeader().name().equals(MqttVersion.MQTT_3_1_1)){
            ch.close();
            return;
        }

        //MQTT_3.1.2-2
        if(mqttConnectMessage.variableHeader().version() != DovakinConstants.PROTOCOL_VERSION){
            MqttConnAckMessage connAckMessage = MqttMessageBuilder.buildConnAckMessage(
                    MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION,
                    mqttConnectMessage.fixedHeader().qosLevel(),
                    mqttConnectMessage.variableHeader().isCleanSession());
            return;
        }

        if(mqttConnectMessage.variableHeader().isCleanSession()){

        }

        //TODO CONNECT 报文有效性验证

        ConnectionStore connectionStore = server.getConnectionStore();
        MqttConnection connection = new MqttConnection(
                mqttConnectMessage.payload().clientIdentifier(),
                mqttConnectMessage.variableHeader().isCleanSession(),
                ch);
        connectionStore.addConnection(connection);

        MqttConnAckMessage connAckMessage =MqttMessageBuilder.buildConnAckMessage(
                MqttConnectReturnCode.CONNECTION_ACCEPTED,
                MqttQoS.AT_LEAST_ONCE,
                false);
        connection.send(connAckMessage);
    }
}

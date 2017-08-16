package io.dovakinmq.processor.impl;

import io.dovakinmq.DovakinConst;
import io.dovakinmq.manager.ConnectionStore;
import io.dovakinmq.manager.MqttConnection;
import io.dovakinmq.mqtt.builder.MqttMessageBuilder;
import io.dovakinmq.processor.Processor;
import io.dovakinmq.server.MQServer;
import io.dovakinmq.validator.RequestRecorder;
import io.dovakinmq.validator.impl.ConnectValidator;
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
                ch.attr(DovakinConst.RECORDER_ATTRIBUTE_KEY);
        RequestRecorder requestRecorder = attribute.get();
        requestRecorder.record(mqttMessageType);
        switch (mqttMessageType){
            case CONNECT:
                connect((MqttConnectMessage)mqttMessage, ch, requestRecorder);
                break;
        }
    }

    private void connect(MqttConnectMessage mqttConnectMessage, Channel ch, RequestRecorder recorder){

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

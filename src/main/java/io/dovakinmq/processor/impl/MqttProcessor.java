package io.dovakinmq.processor.impl;

import io.dovakinmq.cache.MqttConnectionStore;
import io.dovakinmq.cache.SubscriptionCache;
import io.dovakinmq.constant.DovakinConstants;
import io.dovakinmq.ConnectionStore;
import io.dovakinmq.cache.MqttConnection;
import io.dovakinmq.mqtt.builder.MqttMessageBuilder;
import io.dovakinmq.Processor;
import io.dovakinmq.MQServer;
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
            case PUBLISH:
                publish((MqttPublishMessage)mqttMessage, ch, requestRecorder);
                break;
            case SUBSCRIBE:
                subscribe((MqttSubscribeMessage)mqttMessage, ch, requestRecorder);
                break;
            case PINGREQ:
                pingresp(ch);
                break;

        }
    }

    private void subscribe(MqttSubscribeMessage mqttSubscribeMessage, Channel ch, RequestRecorder recorder){

    }

    private void pingresp(Channel ch){
        MqttMessage mqttMessage = MqttMessageBuilder.buildPingRespMessage();
        ch.writeAndFlush(mqttMessage);
    }

    private void publish(MqttPublishMessage mqttPublishMessage, Channel ch, RequestRecorder recorder){
        SubscriptionCache.publish(mqttPublishMessage);
    }

    private void connect(MqttConnectMessage mqttConnectMessage, Channel ch, RequestRecorder recorder){

        //MQTT_3.1.0-1
        if(recorder.getHistory(0) != MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.0-2
        if(recorder.getHistory(1) != null
                && recorder.getHistory(1) == MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.2-1
        if(!mqttConnectMessage.variableHeader().name().equals(MqttVersion.MQTT_3_1_1.protocolName())){
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

        //ConnectionStore connectionStore = server.getConnectionStore();
        MqttConnection connection = new MqttConnection(
                mqttConnectMessage.payload().clientIdentifier(),
                mqttConnectMessage.variableHeader().isCleanSession(),
                ch);
        MqttConnectionStore.addConnection(connection);

        MqttConnAckMessage connAckMessage =MqttMessageBuilder.buildConnAckMessage(
                MqttConnectReturnCode.CONNECTION_ACCEPTED,
                MqttQoS.AT_LEAST_ONCE,
                false);
        connection.send(connAckMessage);
    }
}

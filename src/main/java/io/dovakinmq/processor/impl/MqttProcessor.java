package io.dovakinmq.processor.impl;

import io.dovakinmq.cache.*;
import io.dovakinmq.constant.ChannelInfo;
import io.dovakinmq.constant.DovakinConstants;
import io.dovakinmq.ConnectionStore;
import io.dovakinmq.mqtt.QoSMessagePack;
import io.dovakinmq.mqtt.builder.MqttMessageBuilder;
import io.dovakinmq.Processor;
import io.dovakinmq.MQServer;
import io.dovakinmq.server.MessageExecutor;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.Attribute;

/**
 * Created by Link on 2017/8/15.
 */
public class MqttProcessor implements Processor<MqttMessage> {

    public MqttProcessor(MQServer server){
    }

    public void process(MqttMessage mqttMessage, Channel ch){
        MqttMessageType mqttMessageType = mqttMessage.fixedHeader().messageType();
        Attribute<RequestRecorder> attribute =
                ch.attr(DovakinConstants.RECORDER_ATTRIBUTE_KEY);
        RequestRecorder requestRecorder = attribute.get();
        requestRecorder.record(mqttMessageType);
        switch (mqttMessageType){
            case CONNECT:
                System.out.println("### CONNECT");
                connect((MqttConnectMessage)mqttMessage, ch, requestRecorder);
                break;
            case PUBLISH:
                System.out.println("### PUBLISH");
                publish((MqttPublishMessage)mqttMessage, ch, requestRecorder);
                break;
            case PUBACK:
                System.out.println("### PUBACK");
                puback((MqttPubAckMessage)mqttMessage, ch);
                break;
            case PUBREC:
                System.out.println("### PUBREC");
                pubrec(mqttMessage, ch);
                break;
            case PUBREL:
                System.out.println("### PUBREL");
                break;
            case PUBCOMP:
                System.out.println("### PUBCOMP");
                pubcomp(mqttMessage, ch);
                break;
            case SUBSCRIBE:
                System.out.println("### SUBSCRIBE");
                subscribe((MqttSubscribeMessage)mqttMessage, ch, requestRecorder);
                break;
            case PINGREQ:
                pingresp(ch);
                break;

        }
    }

    private void pubcomp(MqttMessage mqttPubAckMessage, Channel ch){
        int messageId = ((MqttMessageIdVariableHeader)mqttPubAckMessage.variableHeader()).messageId();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        ChannelInfo channelInfo = attr.get();
        MqttSession session = MqttSessionCache.get(channelInfo.getIdentifier().value());
        if (session == null) return;
        QoSMessagePack messagePack = session.getMessageList().get(messageId);
        if (messagePack == null) return;

        session.removeMessageTask(messageId);
    }

    private void pubrec(MqttMessage mqttPubAckMessage, Channel ch){
        int messageId = ((MqttMessageIdVariableHeader)mqttPubAckMessage.variableHeader()).messageId();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        ChannelInfo channelInfo = attr.get();
        MqttSession session = MqttSessionCache.get(channelInfo.getIdentifier().value());
        if (session == null) return;
        QoSMessagePack messagePack = session.getMessageList().get(messageId);
        if (messagePack == null) return;

        messagePack.setState(QoSMessagePack.STATE.PUBREC);
        session.removeMessageTask(messageId);
        MessageExecutor.put(messagePack);
    }

    private void puback(MqttPubAckMessage mqttPubAckMessage, Channel ch){
        int messageId = mqttPubAckMessage.variableHeader().messageId();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        ChannelInfo channelInfo = attr.get();
        MqttSession session = MqttSessionCache.get(channelInfo.getIdentifier().value());
        if (session == null) return;
        QoSMessagePack messagePack = session.getMessageList().get(messageId);
        if (messagePack == null) return;
        if (messagePack.getLevel() == mqttPubAckMessage.fixedHeader().qosLevel()){
            session.removeMessageTask(messageId);
        }
    }

    private void subscribe(MqttSubscribeMessage mqttSubscribeMessage, Channel ch, RequestRecorder recorder){
        long time1 = System.currentTimeMillis();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        ChannelInfo channelInfo = attr.get();
        SubscriptionCache.subscribe(mqttSubscribeMessage,channelInfo.getIdentifier());
        long time2 = System.currentTimeMillis();
        System.out.println(time2 - time1);
    }

    private void pingresp(Channel ch){
        MqttMessage mqttMessage = MqttMessageBuilder.buildPingRespMessage();
        ch.writeAndFlush(mqttMessage);
    }

    private void publish(MqttPublishMessage mqttPublishMessage, Channel ch, RequestRecorder recorder){
        long time1 = System.currentTimeMillis();
        System.out.println("time1:" + time1);
        SubscriptionCache.publish(mqttPublishMessage);
        long time2 = System.currentTimeMillis();
        System.out.println("time2:" + time2);
        System.out.println(time2 - time1);
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
            ch.writeAndFlush(connAckMessage);
            return;
        }

        if(mqttConnectMessage.variableHeader().isCleanSession()){

        }
        String clientIdentifier = mqttConnectMessage.payload().clientIdentifier();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        attr.setIfAbsent(new ChannelInfo(clientIdentifier));

        MqttConnection connection = new MqttConnection(
                mqttConnectMessage.payload().clientIdentifier(),
                mqttConnectMessage.variableHeader().isCleanSession(),
                ch);
        MqttConnectionStore.addConnection(connection);

        if (mqttConnectMessage.variableHeader().isCleanSession()){
            if(MqttSessionCache.get(clientIdentifier) != null){
                MqttSessionCache.clean(clientIdentifier);
            }
            MqttSession mqttSession = new MqttSession(clientIdentifier);
            MqttSessionCache.put(mqttSession.getClientId(), mqttSession);
            mqttSession.setConnection(connection);
        } else {
            MqttSession session = MqttSessionCache.get(clientIdentifier);
            if(session == null){
                session = new MqttSession(clientIdentifier);
                MqttSessionCache.put(session.getClientId(), session);
                session.setConnection(connection);
            } else {
                session.getConnection().close();
                session.setConnection(connection);
            }
        }

        MqttConnAckMessage connAckMessage =MqttMessageBuilder.buildConnAckMessage(
                MqttConnectReturnCode.CONNECTION_ACCEPTED,
                MqttQoS.AT_LEAST_ONCE,
                false);
        connection.send(connAckMessage);
    }
}

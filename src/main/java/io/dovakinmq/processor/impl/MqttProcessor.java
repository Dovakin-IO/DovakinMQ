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
        /** 请求记录  */
        RequestRecorder requestRecorder = attribute.get();
        requestRecorder.record(mqttMessageType);
        switch (mqttMessageType){
            /** 连接服务器 */
            case CONNECT:
                System.out.println("### CONNECT");
                connect((MqttConnectMessage)mqttMessage, ch, requestRecorder);
                break;
             /** 确认连接请求 */
            case PUBLISH:
                System.out.println("### PUBLISH");
                publish((MqttPublishMessage)mqttMessage, ch, requestRecorder);
                break;
             /** 发布确认 */
            case PUBACK:
                System.out.println("### PUBACK");
                puback((MqttPubAckMessage)mqttMessage, ch);
                break;
             /** 发布收到（QoS 2 , STEP 1） */
            case PUBREC:
                System.out.println("### PUBREC");
                pubrec(mqttMessage, ch);
                break;
             /** 发布释放（QoS 2 , STEP 2） */
            case PUBREL:
                System.out.println("### PUBREL");
                break;
             /** 发布完成（QoS 2 , STEP 3） */
            case PUBCOMP:
                System.out.println("### PUBCOMP");
                pubcomp(mqttMessage, ch);
                break;
             /** 订阅主题 */
            case SUBSCRIBE:
                System.out.println("### SUBSCRIBE");
                subscribe((MqttSubscribeMessage)mqttMessage, ch, requestRecorder);
                break;
            /** 取消订阅 */
            case UNSUBSCRIBE:
                break;
            /** 取消订阅确认 */
            case UNSUBACK:
                break;
             /** 心跳请求 */
            case PINGREQ:
                pingresp(ch);
                break;
            /** 心跳响应 */
            case PINGRESP:
                break;
            /** 断开连接 */
            case DISCONNECT:
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
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        ChannelInfo channelInfo = attr.get();
        SubscriptionCache.subscribe(mqttSubscribeMessage,channelInfo.getIdentifier());
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
        //客户端到服务端的网络连接建立后,客户端发送给服务端的第一个报文必须是CONNECT报文	
        if(recorder.getHistory(0) != MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.0-2
        //在一个网络连接上,客户端只能发送一次CONNECT报文。服务端必须将客户端发送的第二
        //个CONNECT报文当作协议违规处理并断开客户端的连接
        if(recorder.getHistory(1) != null
                && recorder.getHistory(1) == MqttMessageType.CONNECT){
            ch.close();
            return;
        }

        //MQTT_3.1.2-1
        //如果协议名不正确服务端可以断开客户端的连接,也可以按照某些其它规范继续处理
        //CONNECT报文。对于后一种情况,按照本规范,服务端不能继续处理CONNECT报文
        if(!mqttConnectMessage.variableHeader().name().equals(MqttVersion.MQTT_3_1_1.protocolName())){
            ch.close();
            return;
        }

        //MQTT_3.1.2-2
        //客户端用8位的无符号值表示协议的修订版本。对于3.1.1版协议,协议级别字段的值是
        //4(0x04)。如果发现不支持的协议级别,服务端必须给发送一个返回码为0x01(不支持的协议
        //级别)的CONNACK报文响应CONNECT报文,然后断开客户端的连接
        if(mqttConnectMessage.variableHeader().version() != DovakinConstants.PROTOCOL_VERSION){
            MqttConnAckMessage connAckMessage = MqttMessageBuilder.buildConnAckMessage(
                    MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION,
                    mqttConnectMessage.fixedHeader().qosLevel(),
                    mqttConnectMessage.variableHeader().isCleanSession());
            ch.writeAndFlush(connAckMessage);
            return;
        }

        if(mqttConnectMessage.variableHeader().isCleanSession()){
            //TODO
        }

        // 客户端标识符
        String clientIdentifier = mqttConnectMessage.payload().clientIdentifier();
        Attribute<ChannelInfo> attr = ch.attr(DovakinConstants.CHANNEL_INFO_ATTRIBUTE_KEY);
        attr.setIfAbsent(new ChannelInfo(clientIdentifier));

        MqttConnection connection = new MqttConnection(
                mqttConnectMessage.payload().clientIdentifier(),
                mqttConnectMessage.variableHeader().isCleanSession(),
                ch);

        // MQTT-3.1.2-4
        //如果清理会话(CleanSession)标志被设置为0,服务端必须基于当前会话(使用客户端标识
        //符识别)的状态恢复与客户端的通信。如果没有与这个客户端标识符关联的会话,服务端必
        //须创建一个新的会话。在连接断开之后,当连接断开后,客户端和服务端必须保存会话信息

        // 缓存会话
        MqttConnectionStore.addConnection(connection);


        //TODO
        // MQTT-3.1.2-6
        //如果清理会话(CleanSession)标志被设置为1,客户端和服务端必须丢弃之前的任何会话并
        //开始一个新的会话。会话仅持续和网络连接同样长的时间。与这个会话关联的状态数据不能
        //被任何之后的会话重用
        if (mqttConnectMessage.variableHeader().isCleanSession()){
            if(MqttSessionCache.get(clientIdentifier) != null){
                MqttSessionCache.clean(clientIdentifier);
            }
            MqttSession mqttSession = new MqttSession(clientIdentifier);
            MqttSessionCache.put(mqttSession.getClientId(), mqttSession);
            mqttSession.setConnection(connection);
        } 
        //TODO
        // MQTT-3.1.2-5 
        //清理会话标志为0的会话连接断开之后,服务端必须将之后的QoS1和
        //QoS2级别的消息保存为会话状态的一部分,如果这些消息匹配断开连接时客户端的任何订阅
        else {
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

        // MQTT-3.2.0-1
        //服务端发送CONNACK报文响应从客户端收到的CONNECT报文。服务端发送给客户端的第
        //一个报文必须是CONNACK
        MqttConnAckMessage connAckMessage =MqttMessageBuilder.buildConnAckMessage(
                MqttConnectReturnCode.CONNECTION_ACCEPTED,
                MqttQoS.AT_LEAST_ONCE,
                false);
        connection.send(connAckMessage);
    }
}

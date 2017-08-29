package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;
import io.dovakinmq.mqtt.QoSMessagePack;
import io.dovakinmq.mqtt.Topic;
import io.dovakinmq.server.MessageExecutor;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by link on 2017/8/17.
 */
public class MqttSession {
    private ClientIdentifier identifier;
    private List<Topic> subscription;
    private ConcurrentHashMap<Integer, QoSMessagePack> messagePackList;
    private MqttConnection connection;

    public MqttSession(String id){
        identifier = new ClientIdentifier(id);
        subscription = new ArrayList<Topic>();
        messagePackList = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer, QoSMessagePack> getMessageList(){
        return messagePackList;
    }

    public void removeMessageTask(int id){
        messagePackList.remove(id);
    }

    public String getClientId(){
        return identifier.value();
    }

    public void sendMessage(MqttMessage message){
        QoSMessagePack messagePack = new QoSMessagePack(
                connection,
                message);
        MessageExecutor.put(messagePack);
    }

    public void addMessage(MqttMessage message){
        QoSMessagePack messagePack = new QoSMessagePack(
                connection,
                message);
        messagePackList.putIfAbsent(messagePack.id(),messagePack);
    }

    public void addMessage(QoSMessagePack messagePack){
        messagePackList.putIfAbsent(messagePack.id(),messagePack);
    }

    public void setConnection(MqttConnection connection){
        this.connection = connection;
    }

    public MqttConnection getConnection(){
        return this.connection;
    }
}

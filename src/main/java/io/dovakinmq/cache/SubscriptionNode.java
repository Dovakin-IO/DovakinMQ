package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;
import io.dovakinmq.mqtt.Topic;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by link on 2017/8/23.
 */
public class SubscriptionNode {
    private Topic.Element topic;
    private SubscriptionNode preNode;
    private ConcurrentHashMap<Topic.Element, SubscriptionNode> subNodes;
    private Vector<ClientIdentifier> matchedClients;
    private Vector<ClientIdentifier> multiClients;
    private Vector<ClientIdentifier> singleClients;

    public SubscriptionNode(Topic.Element topic, SubscriptionNode preNode){
        this.topic = topic;
        this.preNode = preNode;
        this.matchedClients = new Vector<ClientIdentifier>();
        this.multiClients = new Vector<ClientIdentifier>();
        this.singleClients = new Vector<ClientIdentifier>();
        subNodes = new ConcurrentHashMap<Topic.Element, SubscriptionNode>();
    }

    public Topic.Element getTopic(){
        return topic;
    }

    public SubscriptionNode getNode(Topic.Element topic){
        SubscriptionNode subNode = subNodes.get(topic);
        return subNode;
    }

    public SubscriptionNode addNode(Topic.Element topic){
        SubscriptionNode subNode = new SubscriptionNode(topic,this);
        SubscriptionNode var = subNodes.putIfAbsent(subNode.getTopic(), subNode);
        return var == null ? subNode : var;
    }

    public Vector<ClientIdentifier> getMatchedClients() {
        return matchedClients;
    }

    public Vector<ClientIdentifier> getMultiClients() {
        return multiClients;
    }

    public Vector<ClientIdentifier> getSingleClients() {
        return singleClients;
    }



    public boolean isRoot(){
        return preNode == null ? true : false;
    }

    public void publish(MqttPublishMessage message, boolean isTail){
        if(isTail){
            for(ClientIdentifier identifier : matchedClients){
                MqttSession session = MqttSessionCache.get(identifier.value());
                if(session != null) session.sendMessage(message);
/*                MqttConnection connection
                        = MqttConnectionStore.getConnection(identifier.value());
                connection.send(message);*/
            }
        }
        for(ClientIdentifier identifier : multiClients){
            MqttSession session = MqttSessionCache.get(identifier.value());
            if(session != null) session.sendMessage(message);
        }
        for(ClientIdentifier identifier : singleClients){
            MqttSession session = MqttSessionCache.get(identifier.value());
            if(session != null) session.sendMessage(message);
        }
    }
}

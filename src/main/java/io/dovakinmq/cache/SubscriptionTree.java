package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;
import io.dovakinmq.mqtt.Topic;
import io.netty.handler.codec.mqtt.MqttPublishMessage;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class SubscriptionTree {

    private SubscriptionNode node;
    private Object lock;

    public SubscriptionTree(){
        lock = new Object();
    }

    public void add(Topic topic, MqttPublishMessage message){
        synchronized (lock){
            if(node == null || topic == null) return;
            topic.reset();
            if(message != null) node.publish(message,!topic.hasNext());
            buildNodes(node,topic.moveToNext(),message);
        }
    }

    public void build(Topic topic){
        synchronized (lock){
            if(node != null || topic == null) return;
            topic.reset();
            node = new SubscriptionNode(topic.next(),null);
            buildNodes(node,topic,null);
        }
    }

    public void add(Topic topic){
        synchronized (lock){
            if(node == null || topic == null) return;
            topic.reset();
            if(!node.getTopic().equals(topic.next())) return;
            buildNodes(node, topic,null);
        }
    }

    public void subscribe(Topic topic, ClientIdentifier identifier){
        topic.reset();
        if (!node.getTopic().equals(topic.next())) return;
        searchNodes(node, topic, identifier);
    }

    private void searchNodes(SubscriptionNode node, Topic topic, ClientIdentifier identifier){
        Topic.Element element = topic.next();
        if (element.getValue().equals(Topic.MULTI)){
            node.getMultiClients().add(identifier);
            return;
        } else if (element.getValue().equals(Topic.SINGLE)){
            node.getSingleClients().add(identifier);
            return;
        }
        SubscriptionNode subNode = node.getNode(element);
        if(subNode == null)
            subNode = node.addNode(element);
        if(!topic.hasNext()){
            subNode.getMatchedClients().add(identifier);
            return;
        }
        searchNodes(subNode, topic, identifier);
    }

    private void buildNodes(SubscriptionNode node, Topic topic, MqttPublishMessage message){
        Topic.Element element = topic.next();
        if (element == null) return;
        SubscriptionNode var = node.addNode(element);
        if(message != null) var.publish(message, !topic.hasNext());
        buildNodes(var,topic,message);
    }
}

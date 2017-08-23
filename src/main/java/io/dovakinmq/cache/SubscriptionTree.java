package io.dovakinmq.cache;

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
            if(node != null || topic == null) return;
            topic.reset();
            node = new SubscriptionNode(topic.next(),null);
            if(message != null) node.publish(message);
            buildNodes(node,topic,message);
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

    private void buildNodes(SubscriptionNode node, Topic topic, MqttPublishMessage message){
        Topic.Element element = topic.next();
        if (element == null) return;
        SubscriptionNode var = node.addNode(element);
        if(message != null) var.publish(message);
        buildNodes(var,topic,message);
    }
}

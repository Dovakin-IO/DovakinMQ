package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;
import io.dovakinmq.mqtt.Topic;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/23.
 */
public class SubscriptionCache {

    private static final ConcurrentHashMap<String, SubscriptionTree> subscriptions;

    static {
        subscriptions
                = new ConcurrentHashMap<String, SubscriptionTree>();
    }

    public static void publish(MqttPublishMessage message){
        Topic topic = new Topic(message.variableHeader().topicName());
        Topic.Element element = topic.getHeadElement();
        if (element == null) return;
        SubscriptionTree cachedTree = subscriptions.get(element.getValue());
        if(cachedTree == null){
            SubscriptionTree tree = new SubscriptionTree();
            tree.build(topic);
            subscriptions.putIfAbsent(element.getValue(), tree);
        } else {
            cachedTree.add(topic,message);
        }
    }

    public static void subscribe(MqttSubscribeMessage message, ClientIdentifier identifier){
        for(MqttTopicSubscription topicSubscription : message.payload().topicSubscriptions()){
            Topic topic = new Topic(topicSubscription.topicName());
            Topic.Element element = topic.getHeadElement();
            if (element == null) return;
            SubscriptionTree cachedTree = subscriptions.get(element.getValue());
            if(cachedTree == null){
                SubscriptionTree tree = new SubscriptionTree();
                tree.build(topic);
                subscriptions.putIfAbsent(element.getValue(), tree);
                tree.subscribe(topic,identifier);
            } else {
                cachedTree.subscribe(topic,identifier);
            }
        }
    }
}

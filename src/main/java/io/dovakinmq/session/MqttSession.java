package io.dovakinmq.session;

import io.dovakinmq.manager.ClientIdentifier;
import io.dovakinmq.validator.RequestRecorder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class MqttSession {
    private ClientIdentifier identifier;
    private RequestRecorder requestRecorder;
    private ConcurrentHashMap<String, Subscription> subscriptions;

    public MqttSession(String id){
        identifier = new ClientIdentifier(id);
        requestRecorder = new RequestRecorder();
        subscriptions = new ConcurrentHashMap<String, Subscription>();
    }

    public RequestRecorder getRequestRecorder() {
        return requestRecorder;
    }

    public void subscribe(String topic, Subscription subscription){
        subscriptions.putIfAbsent(topic,subscription);
    }
}

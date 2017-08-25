package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;


/**
 * Created by link on 2017/8/17.
 */
public class MqttSession {
    private ClientIdentifier identifier;
    private RequestRecorder requestRecorder;
    //private ConcurrentHashMap<String, Subscription> subscriptions;

    public MqttSession(String id){
        identifier = new ClientIdentifier(id);
        requestRecorder = new RequestRecorder();
        //subscriptions = new ConcurrentHashMap<String, Subscription>();
    }

    public RequestRecorder getRequestRecorder() {
        return requestRecorder;
    }

/*    public void subscribe(String topic, Subscription subscription){
        subscriptions.putIfAbsent(topic,subscription);
    }*/
}

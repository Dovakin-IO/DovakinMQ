package io.dovakinmq.cache;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Link on 2017/8/16.
 */
public class RequestRecorder {

    private List<MqttMessageType> history;

    public RequestRecorder(){
        history = new ArrayList<MqttMessageType>();
    }

    public void record(MqttMessageType type){
        history.add(type);
    }

    public MqttMessageType getHistory(int index){
        return (index > history.size() -1) ? null : history.get(index);
    }
}

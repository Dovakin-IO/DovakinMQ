package io.dovakinmq.validator;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhuanchao on 2017/8/16.
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
        if(index > history.size() - 1) return null;
        return history.get(index);
    }
}

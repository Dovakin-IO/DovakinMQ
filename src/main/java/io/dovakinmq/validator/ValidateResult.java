package io.dovakinmq.validator;

import io.netty.handler.codec.mqtt.MqttMessage;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class ValidateResult {
    private boolean isValid;
    private String nextNode;
    private MqttMessage mqttMessage;

    public ValidateResult(){
        this.nextNode = "";
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getNext() {
        return nextNode;
    }

    public void setNext(String next) {
        this.nextNode = next;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }
}

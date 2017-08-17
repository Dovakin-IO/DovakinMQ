package io.dovakinmq.validator;

import io.dovakinmq.annotation.MqttValidate;

import java.lang.reflect.Method;

/**
 * Created by liuhuanchao on 2017/8/17.
 */
public class ValidateNode {

    MqttValidate mqttValidate;
    Method method;

    public MqttValidate getMqttValidate() {
        return mqttValidate;
    }

    public void setMqttValidate(MqttValidate mqttValidate) {
        this.mqttValidate = mqttValidate;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}

package io.dovakinmq.annotation;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.lang.annotation.*;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validator {

    MqttMessageType type();
}

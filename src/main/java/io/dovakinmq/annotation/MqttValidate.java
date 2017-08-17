package io.dovakinmq.annotation;

import java.lang.annotation.*;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MqttValidate {

    String ROOT_NAME = "root";

    String[] protocol() default {};
    String nodeName();
    String preNodeName() default ROOT_NAME;
    boolean isTail() default false;
}

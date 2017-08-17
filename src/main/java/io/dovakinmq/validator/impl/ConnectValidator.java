package io.dovakinmq.validator.impl;

import io.dovakinmq.annotation.MqttValidate;
import io.dovakinmq.annotation.Validator;
import io.dovakinmq.constant.DovakinConstants;
import io.dovakinmq.constant.MqttCriterion;
import io.dovakinmq.mqtt.builder.MqttMessageBuilder;
import io.dovakinmq.validator.MqttValidator;
import io.dovakinmq.validator.RequestRecorder;
import io.dovakinmq.validator.ValidateResult;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
@Validator(type = MqttMessageType.CONNECT)
public class ConnectValidator implements MqttValidator {

    private MqttConnectMessage message;
    private Channel channel;
    private RequestRecorder requestRecorder;

    private final String VALIDATE_1 = "valitate_1";
    private final String VALIDATE_2 = "validate_2";
    private final String VALIDATE_3 = "validate_3";
    private final String VALIDATE_4 = "validate_4";


    public void init(MqttMessage mqttMessage, Channel channel, RequestRecorder recorder) {
        this.message = (MqttConnectMessage)mqttMessage;
        this.channel = channel;
        this.requestRecorder = recorder;
    }

    @MqttValidate(protocol
            = {MqttCriterion.MQTT_3_1_0_1,
            MqttCriterion.MQTT_3_1_0_2},
                nodeName = VALIDATE_1)
    public ValidateResult validate_1(ValidateResult result){
        //MQTT_3.1.0-1
        if(requestRecorder.getHistory(0) != MqttMessageType.CONNECT){
            result.setValid(false);
            channel.close();
            return result;
        }

        //MQTT_3.1.0-2
        if(requestRecorder.getHistory(1) == null
                || requestRecorder.getHistory(1) == MqttMessageType.CONNECT){
            result.setValid(false);
            channel.close();
            return result;
        }

        result.setValid(true);
        result.setNext(VALIDATE_2);
        return result;
    }

    @MqttValidate(protocol = {MqttCriterion.MQTT_3_1_2_1},
            nodeName = VALIDATE_2, preNodeName = VALIDATE_1)
    public ValidateResult validate_2(ValidateResult result){
        //MQTT_3.1.2-1
        if(!message.variableHeader().name().equals("MQTT")){
            result.setValid(false);
            result.setNext(VALIDATE_3);
            return result;
        }

        //MQTT_3.1.2-2
        if(message.variableHeader().version() != DovakinConstants.PROTOCOL_VERSION){
            result.setValid(false);
            result.setNext(VALIDATE_4);
        }

        //MQTT_3.1.2-3
        //TODO Reserved

        result.setValid(true);
        return result;
    }

    @MqttValidate(nodeName = VALIDATE_3, isTail = true)
    public ValidateResult validate_3(ValidateResult result){
        channel.close();
        result.setValid(false);
        return result;
    }

    @MqttValidate(nodeName = VALIDATE_4, isTail = true)
    public ValidateResult validate_4(ValidateResult result){
        MqttConnAckMessage connAckMessage = MqttMessageBuilder.buildConnAckMessage(
                MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION,
                message.fixedHeader().qosLevel(),
                message.variableHeader().isCleanSession());
        result.setValid(false);
        result.setMqttMessage(connAckMessage);
        return result;
    }
}

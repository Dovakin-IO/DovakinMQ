package io.dovakinmq.mqtt;

import io.dovakinmq.cache.MqttConnection;
import io.dovakinmq.manager.ClientIdentifier;
import io.netty.handler.codec.mqtt.*;

/**
 * Created by liuhuanchao on 2017/8/26.
 */
public class QoSMessagePack {
    private ClientIdentifier identifier;
    private int messageId;
    private MqttQoS level;
    private MqttMessage mqttMessage;
    private MqttConnection connection;
    private STATE state;

    private int attempTimes;
    private int MAX_ATTEMP_TIMES = 10;
    private long lastExecuteTime;

    public int id(){
        return messageId;
    }

    public enum STATE{
        START(0),
        PUBLISH(1),
        PUBACK(2),
        PUBREC(3),
        PUBREL(4),
        PUBCOMP(5),
        DONE(6);

        private final int value;

        STATE(int value) {
            this.value = value;
        }

        public static STATE valueOf(int value){
            for (STATE s: values()) {
                if (s.value == value) {
                    return s;
                }
            }
            throw new IllegalArgumentException("invalid Message State: " + value);
        }
    }

    public QoSMessagePack(MqttConnection connection, MqttMessage message){
        if(message instanceof MqttPublishMessage){
            this.messageId = ((MqttPublishMessage) message).variableHeader().packetId();
        }
        this.identifier = connection.getClientIdentifier();
        this.connection = connection;
        this.mqttMessage = message;
        this.level = message.fixedHeader().qosLevel();
        this.state = STATE.START;
        attempTimes = 0;
    }

    public void process(){
        lastExecuteTime = System.currentTimeMillis();
        switch (level){
            case AT_MOST_ONCE:
                onQoS0();
                break;
            case AT_LEAST_ONCE:
                onQoS1();
                break;
            case EXACTLY_ONCE:
                onQoS2();
                break;
        }
    }

    private void onQoS0(){
        synchronized (state){
            if(state == STATE.START){
                connection.send(mqttMessage);
                state = STATE.DONE;
            }
        }
    }

    private void onQoS1(){
        synchronized (state){
            switch (state){
                case START:
                    connection.send(mqttMessage);
                    state = STATE.PUBLISH;
                    break;
                case PUBLISH:
                    break;
                case PUBACK:
                    state = STATE.DONE;
                    break;
            }
        }
    }

    private void onQoS1C2B(){

    }

    private void onQoS2(){
        synchronized (state){
            switch (state){
                case START:
                    connection.send(mqttMessage);
                    state = STATE.PUBLISH;
                    break;
                case PUBLISH:
                    break;
                case PUBREC:
                    MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL,
                            false,
                            MqttQoS.EXACTLY_ONCE,
                            mqttMessage.fixedHeader().isRetain(),
                            0);
                    MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
                    MqttPubAckMessage message = new MqttPubAckMessage(fixedHeader, variableHeader);
                    connection.send(message);
                    state = STATE.PUBREL;
                    break;
                case PUBREL:
                    break;
                case PUBCOMP:
                    state = STATE.DONE;
                    break;
            }
        }
    }

    private void onQoS2C2B(){

    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        synchronized (state){
            this.state = state;
        }
    }

    public ClientIdentifier getClientId() {
        return identifier;
    }

    public long getLastExecuteTime(){
        return lastExecuteTime;
    }

    public MqttQoS getLevel() {
        return level;
    }

}

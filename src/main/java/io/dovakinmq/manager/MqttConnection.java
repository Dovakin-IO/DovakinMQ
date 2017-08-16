package io.dovakinmq.manager;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Link on 2017/8/15.
 */
public class MqttConnection {

    public enum State{
        ESTABLISHED,
        DISCONNECTED
    }

    // 连接状态
    private final AtomicReference<State> connectionState
            = new AtomicReference<State>(State.DISCONNECTED);

    private ClientIdentifier clientIdentifier;
    private Boolean isCleanSession;

    private Channel channel;

    public MqttConnection(String clientId, Boolean isCleanSession, Channel channel){
        clientIdentifier = new ClientIdentifier(clientId);
        this.isCleanSession = isCleanSession;
        this.channel = channel;
    }

    public boolean updateState(State expect, State update){
        return connectionState.compareAndSet(expect,update);
    }

    public void abort(){
        this.channel.close();
    }

    public ClientIdentifier getClientIdentifier() {
        return clientIdentifier;
    }

    public Boolean getCleanSession() {
        return isCleanSession;
    }

    public Channel getChannel() {
        return channel;
    }

    public boolean close(){
        channel.close();
        return true;
    }

    public void send(MqttMessage message){
        channel.writeAndFlush(message);
    }
}

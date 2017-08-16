package io.dovakinmq.manager;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class MqttConnectionStore implements ConnectionStore<MqttConnection>{

    private ConcurrentHashMap<String, MqttConnection> connections;

    public MqttConnectionStore(){
        connections = new ConcurrentHashMap<String, MqttConnection>();
    }

    public int size() {
        return connections.size();
    }

    public MqttConnection addConnection(MqttConnection instance) {
        return connections.putIfAbsent(instance.getClientIdentifier().value(), instance);
    }

    public boolean removeConnection(MqttConnection instance) {
        return connections.remove(instance.getClientIdentifier().value(), instance);
    }

    public MqttConnection getConnection(String identifier) {
        return connections.get(identifier);
    }

    public boolean isAlive(MqttConnection instance) {
        return connections.contains(instance);
    }

    public boolean isAlive(ClientIdentifier identifier) {
        return connections.containsKey(identifier);
    }

    public boolean closeConnection(MqttConnection instance) {
        return instance.close();
    }

    public boolean closeConnection(ClientIdentifier identifier) {
        MqttConnection connection =this.getConnection(identifier.value());
        if(connection == null) return false;
        return connection.close();
    }
}

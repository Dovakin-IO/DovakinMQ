package io.dovakinmq.cache;

import io.dovakinmq.manager.ClientIdentifier;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class MqttConnectionStore{

    private static ConcurrentHashMap<String, MqttConnection> connections;

    static {
        connections = new ConcurrentHashMap<>();
    }

    public static int size() {
        return connections.size();
    }

    public static MqttConnection addConnection(MqttConnection instance) {
        return connections.putIfAbsent(instance.getClientIdentifier().value(), instance);
    }

    public static boolean removeConnection(MqttConnection instance) {
        return connections.remove(instance.getClientIdentifier().value(), instance);
    }

    public static MqttConnection getConnection(String identifier) {
        return connections.get(identifier);
    }

    public static boolean isAlive(MqttConnection instance) {
        return connections.contains(instance);
    }

    public static boolean isAlive(ClientIdentifier identifier) {
        return connections.containsKey(identifier);
    }

    public static boolean closeConnection(MqttConnection instance) {
        return instance.close();
    }

    public static boolean closeConnection(ClientIdentifier identifier) {
        MqttConnection connection = getConnection(identifier.value());
        if(connection == null) return false;
        return connection.close();
    }
}

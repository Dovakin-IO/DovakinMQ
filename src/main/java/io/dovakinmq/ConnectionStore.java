package io.dovakinmq;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public interface ConnectionStore<T> {

    int size();

    T addConnection(T instance);

    boolean removeConnection(T instance);

    T getConnection(String identifier);

    boolean isAlive(T instance);

    boolean closeConnection(T instance);
}

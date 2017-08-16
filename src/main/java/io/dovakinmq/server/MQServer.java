package io.dovakinmq.server;

import io.dovakinmq.manager.ConnectionStore;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public interface MQServer {
    void start();
    ConnectionStore getConnectionStore();
}

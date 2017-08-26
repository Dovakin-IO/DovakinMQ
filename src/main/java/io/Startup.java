package io;

import io.dovakinmq.server.DovakinMQServer;

/**
 * Created by liuhuanchao on 2017/8/26.
 */
public class Startup {
    public static void main(String[] args){
        DovakinMQServer server = new DovakinMQServer(1883);
        server.start();
    }
}

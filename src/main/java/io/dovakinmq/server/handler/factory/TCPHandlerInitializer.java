package io.dovakinmq.server.handler.factory;

import io.dovakinmq.processor.impl.MqttProcessor;
import io.dovakinmq.server.MQServer;
import io.dovakinmq.server.handler.MqttHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class TCPHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private MQServer server;

    public TCPHandlerInitializer(MQServer server){
        this.server = server;
    }

    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("idleState", new IdleStateHandler(90,0,0, TimeUnit.SECONDS));
        pipeline.addLast("mqttDecoder", new MqttDecoder());
        pipeline.addLast("mqttEncoder", MqttEncoder.INSTANCE);
        pipeline.addLast("mqttHandler", new MqttHandler(new MqttProcessor(server)));
    }
}

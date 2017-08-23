package io.dovakinmq.server;

import io.dovakinmq.ConnectionStore;
import io.dovakinmq.MQServer;
import io.dovakinmq.cache.MqttConnectionStore;
import io.dovakinmq.server.handler.factory.TCPHandlerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class DovakinMQServer implements MQServer {

    private int port;

    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private EventLoopGroup bossGroup = new NioEventLoopGroup();


    public DovakinMQServer(int port){
        this.port = port;
    }

    public DovakinMQServer(int port, ConnectionStore connectionStore){
        this.port = port;
    }

    public void start(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(workerGroup,bossGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TCPHandlerInitializer(this))
                .option(ChannelOption.SO_BACKLOG, 512)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        Channel serverChannel = bootstrap.bind(new InetSocketAddress(port)).channel();
        ChannelFuture future = serverChannel.closeFuture();
        try {
            System.out.println("MQTT服务器已启动...");
            future.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

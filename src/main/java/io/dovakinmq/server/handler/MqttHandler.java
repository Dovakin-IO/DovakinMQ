package io.dovakinmq.server.handler;

import io.dovakinmq.cache.RequestRecorder;
import io.dovakinmq.constant.DovakinConstants;
import io.dovakinmq.Processor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.Attribute;

/**
 * Created by Link on 2017/8/15.
 */
public class MqttHandler extends ChannelInboundHandlerAdapter{

    private Processor mProcessor;

    public MqttHandler(Processor processor){
        this.mProcessor = processor;
    }

    private MqttHandler(){}

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Attribute<RequestRecorder> attr = ctx.channel().attr(DovakinConstants.RECORDER_ATTRIBUTE_KEY);
        RequestRecorder var1 = attr.get();
        if(var1 == null){
            RequestRecorder var2 = new RequestRecorder();
            var1 = attr.setIfAbsent(var2);
        }

        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MqttMessage mqttMessage = (MqttMessage) msg;
        mProcessor.process(mqttMessage, ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}

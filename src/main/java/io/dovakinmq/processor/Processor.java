package io.dovakinmq.processor;

import io.netty.channel.Channel;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public interface Processor<T> {

    void process(T obj, Channel channel);
}

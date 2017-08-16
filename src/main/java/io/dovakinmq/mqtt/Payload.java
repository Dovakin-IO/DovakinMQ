package io.dovakinmq.mqtt;

import io.netty.buffer.ByteBuf;

/**
 * Created by liuhuanchao on 2017/8/15.
 */
public class Payload {
    private ByteBuf value;

    public ByteBuf getValue() {
        return value;
    }

    public void setValue(ByteBuf value) {
        this.value = value;
    }
}

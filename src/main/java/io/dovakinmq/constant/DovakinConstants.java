package io.dovakinmq.constant;

import io.dovakinmq.cache.RequestRecorder;
import io.netty.util.AttributeKey;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public class DovakinConstants {
    public static final int PROTOCOL_VERSION = 0x04;
    public static final AttributeKey<RequestRecorder> RECORDER_ATTRIBUTE_KEY
            = AttributeKey.valueOf("request.recorder");
    public static final AttributeKey<ChannelInfo> CHANNEL_INFO_ATTRIBUTE_KEY
            = AttributeKey.valueOf("channel.info");
}

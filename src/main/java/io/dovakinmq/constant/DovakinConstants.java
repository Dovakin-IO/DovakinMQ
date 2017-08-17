package io.dovakinmq.constant;

import io.dovakinmq.validator.RequestRecorder;
import io.netty.util.AttributeKey;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public class DovakinConstants {
    public static final int PROTOCOL_VERSION = 0x04;
    public static final AttributeKey<RequestRecorder> RECORDER_ATTRIBUTE_KEY
            = AttributeKey.valueOf("request.recorder");
}

package io.dovakinmq.constant;

/**
 * Created by liuhuanchao on 2017/8/16.
 */
public class MqttCriterion {
    /**
     * MQTT_3.1.0-1
     * 客户端到服务端的网络连接建立后，客户端发送给服务端的第一个报文必须是 CONNECT 报文
     */
    public final static String MQTT_3_1_0_1 = "MQTT-3.1.0-1";

    /**
     * MQTT_3.1.0-2
     * 在一个网络连接上，客户端只能发送一次 CONNECT 报文。
     * 服务端必须将客户端发送的第二个 CONNECT 报文当作协议违规处理并断开客户端的连接
     */
    public final static String MQTT_3_1_0_2 = "MQTT-3.1.0-2";

    /**
     * MQTT-3.1.2-1
     * 如果协议名不正确服务端可以断开客户端的连接，也可以按照某些其它规范继续处理 CONNECT 报文。
     * 对 于后一种情况，按照本规范，服务端不能继续处理 CONNECT 报文
     */
    public final static String MQTT_3_1_2_1 = "MQTT-3.1.2-1";
}

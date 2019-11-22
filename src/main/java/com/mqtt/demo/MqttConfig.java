package com.mqtt.demo;

import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MqttConfig {

    @Value("${mqtt.host}")
    private String serviceHost;

    @Value("${mqtt.port}")
    private int servicePort;

    @Value("${mqtt.clientId}")
    private String clientId;

    @Value("${mqtt.cleanStart}")
    private boolean cleanStart;

    @Value("${mqtt.reconnectionAttemptMax}")
    private int reconnectionAttemptMax;

    @Value("${mqtt.reconnectionDelay}")
    private int reconnectionDelay;

    @Value("${mqtt.keepAlive}")
    private short keepAlive;

    @Value("${mqtt.sendBufferSize}")
    private int sendBufferSize;

    @Value("${mqtt.receiveBufferSize}")
    private int receiveBufferSize;

    @Bean
    public MQTT getMQTT() throws URISyntaxException {
        MQTT mqtt = new MQTT();
        mqtt.setHost(serviceHost, servicePort);
        /*用于设置客户端会话的ID。在setCleanSession(false);被调用时，MQTT服务器利用该ID获得相应的会话。
         *此ID应少于23个字符，默认根据本机地址、端口和时间自动生成
         */
        mqtt.setClientId(clientId);
        /*
        * 如果为false(flag=0)，Client断开连接后，Server应该保存Client的订阅信息，但QoS状态需要为1 或 2。
        * 如果为true(flag=1)，表示Server应该立刻丢弃任何会话状态信息。
        * 若设为false，MQTT服务器将持久化客户端会话的主体订阅和ACK位置，默认为true
        * */
        mqtt.setCleanSession(cleanStart);
        //设置重新连接的次数
        mqtt.setReconnectAttemptsMax(reconnectionAttemptMax);
        //设置重连的间隔时间,默认为10ms
        mqtt.setReconnectDelay(reconnectionDelay);
        /*设置心跳时间，表示响应时间，如果这个时间内，连接或发送操作未完成，则断开tcp连接，表示离线，如果设为0则是一直连接。
         *定义客户端传来消息的最大时间间隔秒数，服务器可以据此判断与客户端的连接是否已经断开，从而避免TCP/IP超时的长时间等待
         */
        mqtt.setKeepAlive(keepAlive);
        //设置socket发送缓冲区大小，默认为65536（64k）
        mqtt.setSendBufferSize(sendBufferSize);
        //设置socket接收缓冲区大小，默认为65536（64k）
        mqtt.setReceiveBufferSize(receiveBufferSize);

        mqtt.setTrafficClass(8);//设置发送数据包头的流量类型或服务类型字段，默认为8，意为吞吐量最大化传输

        //带宽限制设置说明
        mqtt.setMaxReadRate(0);//设置连接的最大接收速率，单位为bytes/s。默认为0，即无限制
        mqtt.setMaxWriteRate(0);//设置连接的最大发送速率，单位为bytes/s。默认为0，即无限制

        //设置“遗嘱”消息的话题，若客户端与服务器之间的连接意外中断，服务器将发布客户端的“遗嘱”消息
        mqtt.setWillTopic(String.format("WillTopic/Server/%s", clientId));
        mqtt.setWillMessage(String.format("{\"state\": \"offline\", \"dateTime\": \"%s\",\"type\": \"willMessage\"}",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));//设置“遗嘱”消息的内容，默认是长度为零的消息
        mqtt.setWillQos(QoS.AT_MOST_ONCE);//设置“遗嘱”消息的QoS，默认为QoS.ATMOSTONCE
        mqtt.setWillRetain(true);//若想要在发布“遗嘱”消息时拥有retain选项，则为true

        return mqtt;
    }
}
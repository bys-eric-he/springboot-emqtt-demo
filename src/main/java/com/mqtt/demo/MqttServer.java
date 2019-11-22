package com.mqtt.demo;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eric on 2017/4/24.
 */
public class MqttServer {
    private static Logger logger = Logger.getLogger(MqttServer.class);
    private static MQTT mqtt = ApplicationContextUtil.getBean(MQTT.class);
    private static CallbackConnection callbackConnection = null;

    private static Topic[] topics = {
            new Topic("eric-aaa", QoS.AT_MOST_ONCE),
            new Topic("eric-bbb", QoS.AT_MOST_ONCE),
            new Topic("eric-ccc", QoS.AT_MOST_ONCE),
            new Topic("eric-mqtt-demo", QoS.AT_MOST_ONCE),
            new Topic("CM/DeviceTracingPoint/#", QoS.AT_MOST_ONCE)
    };

    /**
     * 获取一个MQTT连接
     *
     * @return
     */
    public synchronized static CallbackConnection getCallbackConnection() {
        if (callbackConnection == null) {
            callbackConnection = mqtt.callbackConnection();
        }

        return callbackConnection;
    }

    /**
     * 添加监听
     */
    public static void createConnectAndListener() {
        MqttServer.getCallbackConnection().connect(new Callback<Void>() {
            //连接失败
            public void onFailure(Throwable value) {
                logger.info(String.format("MQTTCallbackServer.CallbackConnection.connect.onFailure 连接失败! %n%s"
                        , value.getLocalizedMessage()));
            }

            // 连接成功
            public void onSuccess(Void v) {
                logger.info("--------MQTT Server创建连接成功--------");

                //订阅主题
                MqttServer.subscribe(topics);
            }
        });

        //添加连接的监听事件
        MqttServer.getCallbackConnection().listener(new ExtendedListener() {
            @Override
            public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Callback<Callback<Void>> callback) {
                logger.info("---------接收到消息---------");
                logger.info(String.format("%n消息主题:[%s]%n消息内容:[%s]%n",
                        utf8Buffer.toString(),
                        new String(buffer.toByteArray())));

            }

            @Override
            public void onConnected() {
                logger.info("---------MQTT Server监听连接成功---------");
            }

            @Override
            public void onDisconnected() {
                logger.info("---------MQTT Server监听断开连接--------");
            }

            @Override
            public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.info("===========MQTT Server监听连接失败===========");
                logger.info(String.format("%n消息监听连接失败错误信息： %s", throwable.getMessage()));
            }
        });
    }

    /**
     * 订阅主题
     *
     * @param topics
     */
    public static void subscribe(Topic[] topics) {
        //订阅主题
        MqttServer.getCallbackConnection().subscribe(topics, new Callback<byte[]>() {
            //订阅主题成功
            public void onSuccess(byte[] qoses) {
                logger.info(String.format("--------订阅成功--------%n消息服务质量Qos:%s", qoses[0]));
                logger.info(String.format("------主题->%s", String.join(",", Joiner.on(',').join(topics))));
            }

            //订阅主题失败
            public void onFailure(Throwable value) {
                logger.info(String.format("---------订阅失败--------%n错误信息:%s",
                        value != null ? value.getMessage() : "无错误信息！"));
            }
        });
    }

    /**
     * 取消订阅
     *
     * @param topic
     */
    public static void unsubscribe(String topic) {
        UTF8Buffer[] buffers = {UTF8Buffer.utf8(topic)};
        MqttServer.getCallbackConnection().unsubscribe(buffers, new Callback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                logger.info(String.format("--------取消订阅成功--------%n取消主题：%s", topic));
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.info(String.format("---------订阅失败--------%n错误信息:%s",
                        throwable != null ? throwable.getMessage() : "无错误信息！"));
            }
        });
    }

    /**
     * 发布消息
     *
     * @param topic    消息主题
     * @param message  消息内容
     * @param isRetain 是否保留消息
     */
    public static void publish(String topic, String message, boolean isRetain) {
        //发布消息
        MqttServer.getCallbackConnection().publish(topic,
                message.getBytes(), QoS.EXACTLY_ONCE, isRetain, new Callback<Void>() {
                    public void onSuccess(Void v) {
                        logger.info(String.format("---------消息发布成功--------%n消息主题：%s%n消息内容：%s", topic, message));
                    }

                    public void onFailure(Throwable value) {
                        logger.info(String.format("---------消息发布失败---------%n错误信息：%s",
                                value != null ? value.getMessage() : "无具体错误信息！"));
                    }
                });
    }
}

package com.mqtt.demo;

import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.*;
import org.fusesource.mqtt.codec.MQTTFrame;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MqttDemoApplicationTests {
    @Autowired
    private MQTT mqtt;

    @Test
    public void push() throws Exception {
            /*fusesource提供三种方式实现发布消息的方式：
                1.采用阻塞式的连接的（BlockingConnection）
                2.采用回调式的连接 （CallbackConnection）
                3.采用Future样式的连接（FutureConnection）
                其中，回调API是最复杂的也是性能最好的，
                另外两种均是对回调API的封装。 我们下面就简单介绍一下回调API的使用方法。*/
        //设置跟踪器
        mqtt.setTracer(new Tracer() {
            @Override
            public void onReceive(MQTTFrame frame) {
                System.out.println("receive: " + frame);
            }

            @Override
            public void onSend(MQTTFrame frame) {
                System.out.println("send: " + frame);
            }

            @Override
            public void debug(String message, Object... args) {
                System.out.println(String.format("debug: " + message, args));
            }
        });

        final CallbackConnection callbackConnection = mqtt.callbackConnection();

        //添加监听
        this.addListener(callbackConnection);
        //创建连接
        callbackConnection.connect(new Callback<Void>() {
            //连接失败
            public void onFailure(Throwable value) {
                System.out.println("MQTTCallbackServer.CallbackConnection.connect.onFailure============连接失败：" + value.getLocalizedMessage() + "============");
                value.printStackTrace();
            }

            // 连接成功
            public void onSuccess(Void v) {
                System.out.println("============连接成功==============");
                int count = 1;
                String topic = "mqtt/eric";
                Topic[] topics = {new Topic(topic, QoS.AT_LEAST_ONCE)};

                //订阅主题
                callbackConnection.subscribe(topics, new Callback<byte[]>() {
                    //订阅主题成功
                    public void onSuccess(byte[] qoses) {
                        System.out.println("========订阅成功=======");
                    }

                    //订阅主题失败
                    public void onFailure(Throwable value) {
                        System.out.println("========订阅失败=======");
                        value.printStackTrace();
                        callbackConnection.disconnect(null);
                    }
                });

                while (count < 10) {
                    //发布消息
                    callbackConnection.publish(topic, ("Hello [".concat(Integer.toString(count)).concat("]")).getBytes(), QoS.AT_LEAST_ONCE, true, new Callback<Void>() {
                        public void onSuccess(Void v) {
                            System.out.println("===========消息发布成功============".concat(v.toString()));
                        }

                        public void onFailure(Throwable value) {
                            System.out.println("========消息发布失败=======".concat(value.getMessage()));
                            value.printStackTrace();
                            callbackConnection.disconnect(null);
                        }
                    });
                    count++;
                }
            }
        });

        /*
        FutureConnection connection = mqtt.futureConnection();
        try {
            connection.connect();
            System.out.println("----------Opened MQTT socket, connecting-----------------");
            System.out.println("----------Connected:" + connection.toString() + "-----------");
            int count = 1;
            while (count < 900000000) {
                count++;
                String message = "何涌正在发布消息[ " + count + " ] MQTT...主题是：mqtt/eric";
                String topic = "mqtt-ccc";
                //有三种消息发布服务质量：
                //“至多一次(AT_MOST_ONCE)”，QoS=0：最多一次，消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
                //“至少一次(AT_LEAST_ONCE)”，QoS=1：至少一次，确保消息到达，但消息重复可能会发生。
                //“只有一次(EXACTLY_ONCE)”，QoS=2：只有一次，确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果。
                connection.publish(topic, message.getBytes(), QoS.EXACTLY_ONCE, false);
                System.out.println("MQTTFutureServer.Publish Message " + "Topic Title :" + topic + " context :" + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }*/
    }

    @Test
    public void recive() throws URISyntaxException {
        Topic[] topics = {
                new Topic("mqtt-aaa", QoS.EXACTLY_ONCE),
                new Topic("mqtt/eric", QoS.AT_LEAST_ONCE),
                new Topic("mqtt-ccc", QoS.AT_MOST_ONCE)};

					/*fusesource提供三种方式实现发布消息的方式：
                    1.采用阻塞式的连接的（BlockingConnection）
                    2.采用回调式的连接 （CallbackConnection）
                    3.采用Future样式的连接（FutureConnection）
                    其中，回调API是最复杂的也是性能最好的，
                    另外两种均是对回调API的封装。 我们下面就简单介绍一下回调API的使用方法。*/
        //获取mqtt的连接对象CallbackConnection
        final CallbackConnection callbackConnection = mqtt.callbackConnection();
        try {
            //设置跟踪器
            mqtt.setTracer(new Tracer() {
                @Override
                public void onReceive(MQTTFrame frame) {
                    System.out.println("receive: " + frame);
                }

                @Override
                public void onSend(MQTTFrame frame) {
                    System.out.println("send: " + frame);
                }

                @Override
                public void debug(String message, Object... args) {
                    System.out.println(String.format("debug: " + message, args));
                }
            });

            //添加监听
            this.addListener(callbackConnection);

            //创建连接
            callbackConnection.connect(new Callback<Void>() {
                //连接失败
                public void onFailure(Throwable value) {
                    System.out.println("============连接失败：" + value.getLocalizedMessage() + "============");
                    value.printStackTrace();
                }

                // 连接成功
                public void onSuccess(Void v) {
                    //订阅主题
                    callbackConnection.subscribe(topics, new Callback<byte[]>() {
                        //订阅主题成功
                        public void onSuccess(byte[] qoses) {
                            System.out.println("MQTTSubscribeClient.CallbackConnection.connect.subscribe.onSuccess 订阅主题成功......");
                        }

                        //订阅主题失败
                        public void onFailure(Throwable value) {
                            System.out.println("MQTTSubscribeClient.CallbackConnection.connect.subscribe.onSuccess 订阅主题失败！" + value.getMessage());
                            value.printStackTrace();
                            callbackConnection.disconnect(null);
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            //连接断开
            callbackConnection.disconnect(new Callback<Void>() {
                public void onSuccess(Void v) {
                    // called once the connection is disconnected.
                    System.out.println("MQTTSubscribeClient.CallbackConnection.connect.disconnect.onSuccess called once the connection is disconnected.");
                }

                public void onFailure(Throwable value) {
                    // Disconnects never fail.
                    System.out.println("MQTTSubscribeClient.CallbackConnection.connect.disconnect.onFailure  Disconnects never fail." + value.getMessage());
                    value.printStackTrace();
                }
            });
        }

        /*
        FutureConnection connection = mqtt.futureConnection();
        try {
            connection.connect();
            System.out.println("**************Opened MQTT socket, connecting*****************");
            System.out.println("**************Connected:" + connection.toString() + "***********");
            connection.subscribe(topics);

            System.out.println("...............Listen for messages...................");
            Future<Message> messageFuture = connection.receive();
            Message message = messageFuture.await();
            System.out.println("MQTTFutureClient.Receive Message " + "Topic Title :" + message.getTopic() + " context :"
                    + new String(message.getPayload()));
            message.ack();

        } catch (Exception e) {
            connection.unsubscribe(new String[]{"mqtt-aaa", "mqtt/eric", "mqtt-ccc"});
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }*/
    }


    private void addListener(CallbackConnection callbackConnection) {
        //连接监听
        callbackConnection.listener(new ExtendedListener() {
            @Override
            public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Callback<Callback<Void>> callback) {
                System.out.println("=============receive msg================");
                System.out.println("Topic is [".concat(utf8Buffer.toString()).concat("]") + "content is [".concat(new String(buffer.toByteArray())).concat("]"));
            }

            @Override
            public void onConnected() {
                System.out.println("====mqtt connected=====");
            }

            @Override
            public void onDisconnected() {
                System.out.println("====mqtt disconnected=====");
            }

            @Override
            public void onPublish(UTF8Buffer utf8Buffer, Buffer buffer, Runnable runnable) {

            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println("===========connect failure===========");
                System.out.println("Exception message is [".concat(throwable.getMessage()).concat("]"));
                callbackConnection.disconnect(null);
            }
        });
    }
}
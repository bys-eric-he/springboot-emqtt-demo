package com.mqtt.api;

import com.mqtt.demo.MqttServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * MQTT发布和订阅消息
 * Created by eric on 2017/4/25.
 */
@RestController
@RequestMapping(path = "/api/message")
@Api(description = "MQTT发布和订阅消息")
public class MessageRestful {

    @ApiOperation(value = "订阅消息", nickname = "订阅消息")
    @PostMapping(value = "/subscribe")
    public void subscribe(String... topics) {
        List<Topic> topicList = new ArrayList<>();
        if (topics != null) {
            for (String topic : topics) {
                topicList.add(new Topic(topic, QoS.AT_MOST_ONCE));
            }
            MqttServer.subscribe(topicList.toArray(new Topic[topicList.size()]));
        }
    }

    @ApiOperation(value = "取消订阅", nickname = "取消订阅")
    @PostMapping(value = "/unsubscribe")
    public void unsubscribe(String topic) {
        MqttServer.unsubscribe(topic);
    }

    @ApiOperation(value = "发布消息", nickname = "发布消息")
    @PostMapping(value = "/publish")
    public void publish(String topic, String message, boolean isRetain) {
        MqttServer.publish(topic, message, isRetain);
    }
}
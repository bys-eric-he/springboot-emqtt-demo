package com.mqtt.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Created by eric on 2017/4/24.
 */
@Component
public class Startup implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        //建立MQTT服务监听
        MqttServer.createConnectAndListener();
    }
}

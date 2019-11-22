package com.mqtt.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDateTime;
import java.util.Date;

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@ComponentScan(basePackages = {"com.mqtt.*"})
public class MqttDemoApplication {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MqttDemoApplication.class, args);
        System.out.println("Running Success !!");
    }

    @Scheduled(cron = "0 */1 * * * ?")
    public static void batchPublishMessage() throws Exception{
        for (int i = 0; i < 100; i++) {
            MqttServer.publish(String.format("CM/DeviceTracingPoint/%s", i)
                    , String.format("---------This is the message-%s from topic CM/DeviceTracingPoint/#!-------", i)
                    , false);
        }
    }

    /**
     * Swagger组件注册
     *
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(LocalDateTime.class, Date.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("MQTT DEMO V1.0")
                .description("MQTT消息服务通信")
                .version("1.0")
                .build();
    }
}

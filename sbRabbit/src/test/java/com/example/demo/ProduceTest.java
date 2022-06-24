package com.example.demo;


import com.example.demo.config.RabbitMqConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProduceTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void send(){
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME,"boot.haha","boot 123123132111....");
    }
}

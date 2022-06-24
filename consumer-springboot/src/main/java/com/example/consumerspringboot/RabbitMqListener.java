package com.example.consumerspringboot;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    //定义信息的监听
    @RabbitListener(queues = "boot_queue")
    public void ListQueue(Message message) {
        System.out.println("message" + message);
    }
}

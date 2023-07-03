package Kafk;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    @KafkaListener(topics = "wy", groupId = "my-group")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}

package Kafk;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
public class KafkaProduce {

    @Mock
    private  KafkaTemplate<String, String> kafkaTemplate;



    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}

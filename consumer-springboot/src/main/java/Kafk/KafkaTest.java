package Kafk;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.verify;



@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTest {
    @Mock
    private KafkaProduce producerService;

    @Mock
    private KafkaConsumer consumerService;

    @Test
    public void testSendMessage() throws InterruptedException {
        String message = "Hello, Kafka!";
        producerService.sendMessage("wy", message);
        Thread.sleep(1000);
        verify(consumerService).receiveMessage(message);
    }

}

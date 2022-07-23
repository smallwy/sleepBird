package kafka;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties prop=new Properties();
        prop.put("bootstrap.servers", "106.55.227.38:9092,106.55.227.38:9093,106.55.227.38:9094");
        prop.put("acks", "all");
        prop.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(prop);
        for (int i = 0; i < 10; i++) {
         /*   producer.send((ProducerRecord<String, String>) producer, new Callback() {

                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("同步方式发送消息" + recordMetadata.topic() + "|partition" + recordMetadata.partition());
                }
            });*/
          //
            RecordMetadata recordMetadata = producer.send(new ProducerRecord<String, String>("my-replicate-topic", Integer.toString(i), Integer.toString(i))).get();
        }
      //  RecordMetadata recordMetadata = producer.send(new ProducerRecord<String, String>("my-replicate-topic", Integer.toString(i), Integer.toString(i))).get();

        try {
            Thread.sleep(10000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        producer.close();
    }
}

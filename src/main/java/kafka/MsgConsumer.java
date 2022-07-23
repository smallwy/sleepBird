package kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class MsgConsumer {
    private final static String Topic="my-replicate-topic";
    private final static String CONSUMER_GROUP_NAME="testGroup";

    public static void main(String[] args) {
        Properties prop=new Properties();

        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "106.55.227.38:9092,106.55.227.38:9093,106.55.227.38:9094");
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_NAME);
        prop.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        prop.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10");
        prop.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
        prop.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        prop.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        prop.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30000);
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer= new KafkaConsumer<>(prop);

        consumer.subscribe(Arrays.asList(Topic));
      /*  consumer.assign();*/

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("收到的消息 partition" + record.partition() + "----key" + record.key() + "----value" + record.value());
            }
            if (records.count() > 1) {
                //手动提交  同步提交
                consumer.commitAsync();

                //手动提交 异步提交
               /* consumer.commitAsync(new OffsetCommitCallback() {
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                        if (exception != null) {
                            System.out.println(exception);
                        }
                    }
                });*/
            }
        }
    }

}

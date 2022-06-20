package routing;

import com.rabbitmq.client.*;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer1 {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        final Channel channel = connection.createChannel();
        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        channel.queueDeclare(RabbitConstant.QUEUE_WUHAN, false, false, false, null);
        channel.queueBind(RabbitConstant.QUEUE_WUHAN, RabbitConstant.EXCHANGE_ROUTING, "wuhan2022620");
        //从mq服务器拉取数据
        //1 队列 2 是否自动确认收到消息 false手动确认是否消费 推荐做法 万一出问题  3参数是传入de
        /*   channel.basicConsume(RabbitConstant.QUEUE_HELLOWORLD, false, new Receive(channel));*/
        //从mq服务器拉取数据
        channel.basicConsume(RabbitConstant.QUEUE_WUHAN, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("WUHAN天气收到的气象信息" + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });

    }
}
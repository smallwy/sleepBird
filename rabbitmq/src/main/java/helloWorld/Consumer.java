package helloWorld;

import com.rabbitmq.client.*;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Consumer {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        channel.queueDeclare(RabbitConstant.QUEUE_HELLOWORLD, false, false, false, null);

        //消费一个取一个  消费完再取
        channel.basicQos(1);
        //从Mq服务器拉取数据
        //1 队列 2 是否自动确认收到消息 false手动确认是否消费 推荐做法 万一出问题  3参数是传入de
        channel.basicConsume(RabbitConstant.QUEUE_HELLOWORLD, false, new Receive(channel));
    }
}

class Receive extends DefaultConsumer {
    private Channel channel;

    public Receive(Channel channel) {
        super(channel);
        this.channel = channel;
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String msg = new String(body);
        System.out.println("接受到的消息" + msg);
        System.out.println("接受到的消息标签" + envelope.getDeliveryTag());

        channel.basicAck(envelope.getDeliveryTag(), false);
    }
}

package helloWorld;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

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

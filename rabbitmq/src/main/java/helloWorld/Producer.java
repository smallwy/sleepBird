package helloWorld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer {

    public static void main(String[] args) throws IOException, TimeoutException {

        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        channel.queueDeclare(RabbitConstant.QUEUE_HELLOWORLD, false, false, false, null);

        int i = 0;
        while (i < 100) {
            String msg = "stone" + i;
            //1 交换机信息  2队列 3 额外信息  4 发送的数据
            channel.basicPublish("", RabbitConstant.QUEUE_HELLOWORLD, null, msg.getBytes());
            i++;
        }
        channel.close();
        connection.close();
        System.out.println("数据发送成功");


    }
}

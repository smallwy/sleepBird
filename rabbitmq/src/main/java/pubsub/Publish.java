package pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

//发布订阅模式
public class Publish {
    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        String next = new Scanner(System.in).next();
        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        channel.basicPublish(RabbitConstant.EXCHANGE_PULISH, "", null, next.getBytes());
     /*   //1 队列  2 交换机
        channel.exchangeBind(RabbitConstant.QUEUE_PULISH, RabbitConstant.EXCHANGE_ROUTING, "");*/

        channel.close();
        connection.close();
    }
}

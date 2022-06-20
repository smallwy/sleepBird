package routing;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

//交换机根据routeKey决定 必须与routeKey一一对应 发布订阅模式
public class Publish {
    public final static Map<String, String> info = new HashMap<>();

    static {
        info.put("wuhan2022620", "小雨到中雨");
        info.put("wuhan2022621", "小雨到多雲");

        info.put("guangzhou2022620", "天晴");
        info.put("guangzhou2022621", "天晴转暴雨");
    }


    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            channel.basicPublish(RabbitConstant.EXCHANGE_ROUTING, key, null, value.getBytes());
        }
        channel.close();
        connection.close();
    }
}

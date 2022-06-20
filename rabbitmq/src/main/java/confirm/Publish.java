package confirm;

import com.rabbitmq.client.*;
import utils.RabbitConstant;
import utils.RabbitUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

//交换机根据routeKey决定 必须适应routeKey通配规则  发布订阅模式
public class Publish {
    public final static Map<String, String> info = new HashMap<>();

    static {
        info.put("wuhan.2022620", "小雨到中雨");
        info.put("wuhan.2022621", "小雨到多雲");

        info.put("guangzhou.2022620", "天晴");
        info.put("guangzhou.2022621", "天晴转暴雨");


        info.put("guang111zhou.2022620", "天晴");
        info.put("guang111zhou.2022621", "天晴转暴雨");
    }


    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = RabbitUtils.getConnection();
        Channel channel = connection.createChannel();
        //开启监听模式
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmListener() {

            @Override
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                //第二个参数 表示是否批量接受
                System.out.println("自己的消息被broker接受" + deliveryTag);
            }

            @Override
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("自己的消息被broker拒收" + deliveryTag);
            }
        });

        channel.addReturnListener(new ReturnCallback() {

            @Override
            public void handle(Return returnMessage) {
                System.err.println("======================");
                System.err.println("return"+new String(returnMessage.getBody()));
            }
        });

        //1 队列名称 2是否持久化 3 是否私有化 4 是否自动删除
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            channel.basicPublish(RabbitConstant.EXCHANGE_TOPIC, key, null, value.getBytes());
        }

        //如果关闭 就没法接受监听回调
      /*  channel.close();
        connection.close();*/
    }
}

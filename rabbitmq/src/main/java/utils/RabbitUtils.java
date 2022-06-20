package utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitUtils {

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("106.55.227.38");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("stone");
        connectionFactory.setPassword("stone123");
        connectionFactory.setVirtualHost("/stone");
        return connectionFactory.newConnection();
    }
}

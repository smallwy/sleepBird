package redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.List;

public class JedisSingTest {
    public static void main(String[] args) {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxIdle(5);

        JedisPool jedisPool = new JedisPool(jedisPoolConfig, "120.78.219.38", 6379, 3000, null);

        Jedis jedis = null;
        jedis = jedisPool.getResource();
        jedis.set("sing", "song");
        System.out.println(jedis.get("sing"));
        Pipeline p1 = jedis.pipelined();
        for (int i = 0; i < 10; i++) {
            p1.incr("pipe");
            p1.set("stone" + i, "stone");
        }
        List<Object> objects = p1.syncAndReturnAll();
        System.out.println(objects);
    }
}

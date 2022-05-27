package redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JedisClusterTest {

    public static void main(String[] args) throws IOException {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(20);
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxIdle(5);

        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8001));
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8002));
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8003));
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8004));
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8005));
        jedisClusterNode.add(new HostAndPort("120.78.219.38", 8006));

        JedisCluster jedisCluster = null;
        try {
            jedisCluster = new JedisCluster(jedisClusterNode, 6000, 5000, 10, "stone", jedisPoolConfig);
            System.out.println(jedisCluster.set("stone1", "value1"));
            System.out.println(jedisCluster.get("stone1"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedisCluster.close();
        }
    }
}

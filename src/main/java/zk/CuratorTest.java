package zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Before;


@Slf4j
public class CuratorTest {

    private CuratorFramework curatorFramework = null;

    @Before
    public void curator() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("106.55.227.38:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5200)
                .retryPolicy(retryPolicy)
                .namespace("base")
                .build();
        curatorFramework.start();

    }

    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("106.55.227.38:2181")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5200)
                .retryPolicy(retryPolicy)
                .namespace("base")
                .build();
        curatorFramework.start();

        String s = curatorFramework.create().withMode(CreateMode.PERSISTENT).forPath("/curator‐node", "some‐data".getBytes());
        //log.info("curator create node", s);

    }
}
/*
    @Test
    public void testCreate() throws Exception {

    }
}*/

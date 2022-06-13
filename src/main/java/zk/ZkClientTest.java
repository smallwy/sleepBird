package zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ZkClientTest {

    private static final String ZK_ADRESS = "106.55.227.38:2181";
    private static final int SESSION_TIMEOUT = 5000;
    private static ZooKeeper zooKeeper;
    private static final String ZK_NODE = "/zk_node";

    @Before
    public void init() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(ZK_ADRESS, SESSION_TIMEOUT, watchedEvent -> {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected &&
                    watchedEvent.getType() == Watcher.Event.EventType.None) {
                countDownLatch.countDown();
                log.info("连接成功");
            }
        });
        log.info("链接中。。。。");
        countDownLatch.await();
    }


    @Test
    public void createTest() throws InterruptedException, KeeperException {
        String path = zooKeeper.create(ZK_NODE, "data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        Watcher watcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getType() == Event.EventType.NodeDataChanged && event.getPath() != null && event.getPath().equals(ZK_NODE)) {
                    log.info("change path: {}", event.getPath());
                }
            }
        };

        log.info("created path: {}", path);
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }
}

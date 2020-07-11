package moster.game.testflow;

import moster.app.SimulatorContext;
import moster.app.config.GlobalConfig;
import moster.app.config.TestFlowConfig;
import moster.game.player.PlayerSystem;
import moster.infras.core.ecs.ISystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 测试流程。
 *
 * @author zhangfei
 */
@Component
public class TestFlowSystem implements ISystem {

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 所有测试玩家的信息，key为账号名称，value为对应的玩家信息。
     */
    private final Map<String, TestFlowPlayerInfo> playerInfoMap = new ConcurrentHashMap<>();

    private volatile boolean initialized;

    @Autowired
    private PlayerSystem playerSystem;

    @Scheduled(cron = "0/1 * * * * ?")
    public void check() {
        if (!this.initialized) {
            return;
        }

        long currTime = System.currentTimeMillis();
        long checkInterval = TimeUnit.MINUTES.toMillis(1);

        for (Map.Entry<String, TestFlowPlayerInfo> entry : this.playerInfoMap.entrySet()) {
            TestFlowPlayerInfo info = entry.getValue();
            long offlineTime = info.offlineTime;
            if (offlineTime == -1) {
                // 玩家在线，不需要对它做操作。
                continue;
            }

            if (currTime - offlineTime >= checkInterval) {
                if (info.currWorkFlowTimes >= info.maxWorkFlowTimes) {
                    // 流程测试次数达到限制。
                    this.playerInfoMap.remove(info.accountName);
                    logger.info("玩家[{}]达到了上限次数[{}]的测试流程，将不再触发测试流程", info.accountName, info.maxWorkFlowTimes);
                    continue;
                } else {
                    info.currWorkFlowTimes++;
                }

                this.playerSystem.playerLogin(info.accountName, info.testFlowName);
                info.offlineTime = -1;
            }
        }
    }

    @Override
    public boolean init() {
        GlobalConfig globalConfig = SimulatorContext.getGlobalConfig();
        String accountPrefix = globalConfig.getAccountPrefix();
        List<TestFlowConfig> testFlows = globalConfig.getTestFlows();
        int playerSequence = 1; //用于给玩家指派一个唯一的序号。

        // 根据测试流程来初始化特定数目的玩家信息。
        for (TestFlowConfig testFlow : testFlows) {
            if (!testFlow.isEnabled()) {
                continue;
            }

            int totalNum = testFlow.getPlayerTotal();
            for (int i = 0; i < totalNum; i++) {
                TestFlowPlayerInfo playerInfo = new TestFlowPlayerInfo();
                playerInfo.accountName = accountPrefix + String.format("%06d", playerSequence);
                playerInfo.offlineTime = 0; //设置成0，希望在初始化完成后，立即让玩家执行登入操作。
                playerInfo.testFlowName = testFlow.getName();
                playerInfo.maxWorkFlowTimes = testFlow.getWorkflowTimes();
                this.playerInfoMap.put(playerInfo.accountName, playerInfo);
                playerSequence++;
            }
        }

        if (this.playerInfoMap.size() == 0) {
            logger.warn("没有配置有效的测试流程");
            return false;
        }

        this.initialized = true;
        return true;
    }

    /**
     * 对玩家执行离线操作
     * 一般来说，执行到这个方法，就说明玩家很可能执行了一次完整的流程。
     */
    public void playerOffline(String accountName) {
        TestFlowPlayerInfo playerInfo = this.playerInfoMap.get(accountName);
        if (playerInfo == null) {
            return;
        }

        playerInfo.offlineTime = System.currentTimeMillis();
    }

}

package moster.app;

import com.gameart.dict.DictManager;
import moster.game.decision.DecisionEventManager;
import moster.game.decision.DecisionSystem;
import moster.game.player.PlayerSystem;
import moster.infras.core.context.ServerContext;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.MessageManager;
import moster.infras.util.type.TypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Client模拟器。
 *
 * @author zhangfei
 */
@Component
public class ClientSimulator {

    private static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    @Autowired
    private DecisionSystem decisionSystem;

    @Autowired
    private PlayerSystem playerSystem;

    private ScheduledFuture<?> driveFuture;

    private ScheduledFuture<?> statisticFuture;

    /**
     * 初始化内部状态。
     */
    public boolean init() {
        boolean success = MessageManager.init();
        if (!success) {
            logger.error("消息系统初始化失败");
            return false;
        }

        SimulatorContext.init();

        success = DictManager.getInstance().init(SimulatorContext.globalConfig.getDictHome(), ServerContext.getApplicationContext());
        if (!success) {
            logger.error("字典加载失败");
            return false;
        }

        success = DecisionEventManager.init();
        if (!success) {
            logger.error("决策事件管理器初始化失败");
        }

        // 初始化系统
        List<Class<? extends ISystem>> systemClassList = TypeRegistry.getSubTypes(ISystem.class);
        for (Class<? extends ISystem> systemClass : systemClassList) {
            ISystem system = ServerContext.getBeanIfPresent(systemClass);
            if (system == null) {
                logger.error("系统类[{}]没有被spring管理", systemClass.getName());
                return false;
            }

            // TODO zhangfei 这里没有实现排序。
            success = system.init();
            if (!success) {
                logger.error("系统类[{}]初始化失败", systemClass.getName());
                return false;
            }
        }

        return true;
    }

    /**
     * 开始执行内部逻辑。
     */
    public void start() {
        this.driveFuture = SimulatorContext.generalThreadPool
                .scheduleWithFixedDelay(this.playerSystem::drivePlayers, 3, 1, TimeUnit.SECONDS);

        Runnable statisticTask = () -> {
            int total = ClientSimulator.this.playerSystem.getOnlinePlayersTotal();
            ClientSimulator.logger.info("当前在线玩家数量[{}]", total);
        };

        this.statisticFuture = SimulatorContext.generalThreadPool.scheduleWithFixedDelay(statisticTask, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 停止执行内部逻辑。
     */
    public void stop() {
        this.driveFuture.cancel(false);
        this.statisticFuture.cancel(false);
    }

    /**
     * 执行清理操作。
     */
    public void destroy() throws InterruptedException {
        SimulatorContext.generalThreadPool.shutdown();
        SimulatorContext.generalThreadPool.awaitTermination(30, TimeUnit.SECONDS);

        SimulatorContext.playerExecutorGroup.stop();
    }

}

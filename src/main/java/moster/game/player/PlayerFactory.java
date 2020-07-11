package moster.game.player;

import moster.app.SimulatorContext;
import moster.infras.core.context.ServerContext;
import moster.infras.core.ecs.IComponent;
import moster.infras.core.executor.EventExecutor;
import moster.infras.core.message.MessageProcessor;
import moster.infras.util.type.TypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * 创建玩家的工厂。
 *
 * @author zhangfei
 */
public abstract class PlayerFactory {

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 创建玩家对象。
     *
     * @param accountName 玩家的账号id
     * @param decisionConfig 使用的决策配置
     * @return 如果创建失败就返回null
     */
    public static Player create(String accountName, String workflowName) {
        Player player = new Player();

        // 注册所有组件。
        List<Class<? extends PlayerComponent>> list = TypeRegistry.getSubTypes(PlayerComponent.class);
        for (Class<? extends PlayerComponent> componentClass : list) {
            PlayerComponent componentInstance = null;
            try {
                componentInstance = componentClass.newInstance();
                componentInstance.setOwner(player);
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("生成player[{}]组件实例[{}]失败", accountName, componentClass.getSimpleName(), e);
                return null;
            }
            player.register(componentInstance);
        }

        HashMap<String, String> ctx = new HashMap<>();
        ctx.put("accountName", accountName);
        ctx.put("workflowName", workflowName);

        // 统一加载所有组件。
        for (IComponent component : player.getComponents()) {
            boolean success = component.load(ctx);
            if (!success) {
                logger.warn("玩家[{}]的组件[{}]加载失败", accountName, component.getClass().getSimpleName());
                return null;
            }
        }

        // 设置执行器。
        EventExecutor eventExecutor = SimulatorContext.getPlayerExecutorGroup().borrowExecutor(accountName.hashCode());
        player.setEventExecutor(eventExecutor);

        // 设置消息处理器。
        MessageProcessor messageProcessor = ServerContext.getBean(MessageProcessor.class, player);
        player.setMessageProcessor(messageProcessor);
        return player;
    }

    private PlayerFactory() {

    }

}

package moster.game.decision;

import moster.game.login.LoginComponent;
import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 决策系统。
 *
 * @author zhangfei
 */
@Component
public class DecisionSystem implements ISystem {

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 切换到下一个决策。
     */
    public void switchNext(Player player) {
        DecisionComponent decisionComponent = player.getComponent(DecisionComponent.class);
        decisionComponent.switchNext();
    }

    /**
     * 驱动玩家做出决策。
     *
     * @param player 被驱动的玩家
     * @param currTime 当前时间
     */
    public void drivePlayer(Player player, long currTime) {
        Runnable tickEvent = () -> {
            DecisionComponent decisionComponent = player.getComponent(DecisionComponent.class);
            ActionDecision decision = decisionComponent.currDecision;
            if (decision != null) {
                decision.tick(currTime);
            } else {
                logger.warn("玩家[{}]收到决策事件时当前决策为空", player.getComponent(LoginComponent.class).getAccount());
            }
        };
        player.receive(tickEvent);
    }

    /**
     * 向玩家的当前决策发送事件。
     */
    public void emit(Player player, Object event) {
        DecisionComponent decisionComponent = player.getComponent(DecisionComponent.class);
        DecisionEventManager.emit(decisionComponent.currDecision, event);
    }

}

package moster.game.idle;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Idle决策，让玩家随机处于空闲状态一段时间。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class IdleDecision extends ActionDecision {

    /**
     * 最小空闲时间(毫秒)
     */
    private static final int MIN_IDLE_MILLS = 3000;

    /**
     * 最大空闲时间(毫秒)
     */
    private static final int MAX_IDLE_MILLS = 15000;

    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * idle状态结束的时间。
     */
    private long endTime;

    @Override
    public void tick(long currTime) {
        if (this.endTime == 0) {
            this.endTime = currTime + RandomUtils.nextInt(MIN_IDLE_MILLS, MAX_IDLE_MILLS);
            return;
        }

        if (currTime >= this.endTime) {
            this.decisionSystem.switchNext(super.owner);
        }
    }

    @Override
    public void onEnter() {
        this.endTime = 0;
    }

}

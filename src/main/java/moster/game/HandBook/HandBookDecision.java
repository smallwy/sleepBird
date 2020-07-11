package moster.game.HandBook;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的图鉴决策。
 *
 * @author Gene
 */
@Component
@Scope("prototype")
public class HandBookDecision extends ActionDecision {

    @Autowired
    private HandBookSystem system;

    @Autowired
    private DecisionSystem decisionSystem;

    @Override
    public void tick(long currTime) {
        this.system.getInfo(this.owner);
        this.decisionSystem.switchNext(super.owner);
    }
}

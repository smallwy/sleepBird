package moster.game.login;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionComponent;
import moster.game.player.PlayerSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LogoutDecision extends ActionDecision {

    @Autowired
    private PlayerSystem playerSystem;

    @Override
    public void tick(long currTime) {
        int reason = 1;
        String desc = "正常退出";
        this.playerSystem.playerLogout(super.owner, true, reason, desc);

        DecisionComponent decisionComponent = super.owner.getComponent(DecisionComponent.class);
        decisionComponent.setCurrDecision(null);
    }

}

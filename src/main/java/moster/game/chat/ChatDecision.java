package moster.game.chat;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *  发送聊天
 * @author stone
 */
@Component
@Scope("prototype")
public class ChatDecision extends ActionDecision {


    /**
     * 发送世界聊天
     */
    private boolean haveSendWorldMsg;


    @Autowired
    private DecisionSystem decisionSystem;


    @Autowired
    private ChatSystem system;



    @Override
    public void tick(long currTime) {
        if (!this.haveSendWorldMsg) {
            this.system.sendMsg(this.owner);
            this.haveSendWorldMsg = true;
            return;
        }

        this.decisionSystem.switchNext(super.owner);
    }

    @Override
    public void onEnter() {
        this.haveSendWorldMsg = false;
    }



}

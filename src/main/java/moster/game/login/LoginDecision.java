package moster.game.login;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import moster.game.player.PlayerSystem;
import moster.infras.network.CloseReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的登入决策，让玩家登入到游戏中。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class LoginDecision extends ActionDecision {

    @Autowired
    private LoginSystem loginSystem;

    @Autowired
    private DecisionSystem decisionSystem;

    @Autowired
    private PlayerSystem playerSystem;

    @Override
    public void tick(long currTime) {
        LoginComponent loginComponent = super.owner.getComponent(LoginComponent.class);
        LoginState state = loginComponent.getState();

        switch (state) {
            case UNINITIALIZED:
                this.loginSystem.connectLoginServer(super.owner);
                break;

            case LOGIN_CONNECTING:
                this.loginSystem.checkLoginServerConnect(super.owner);
                break;

            case LOGIN_CONNECTED:
                this.loginSystem.sendLoginServerAuth(super.owner);
                break;

            case LOGIN_AUTHENTICATING:
                this.loginSystem.checkLoginServerAuth(super.owner);
                break;

            case LOGIN_AUTHENTICATED:
                this.loginSystem.connectGateServer(super.owner);
                break;

            case GATE_CONNECTING:
                this.loginSystem.checkGateServerConnect(super.owner);
                break;

            case GATE_CONNECTED:
                this.loginSystem.sendGateServerAuth(super.owner);
                break;

            case GATE_AUTHENTICATING:
                this.loginSystem.checkGateServerAuth(super.owner);
                break;

            case GATE_AUTHENTICATED:
                // 认证成功，需要等到game将所有初始信息推送过来。
                break;

            case SUCCESS:
                this.decisionSystem.switchNext(super.owner);
                break;

            case FAILURE:
            default:
                this.playerSystem.playerLogout(super.owner, true, CloseReason.LOGIN_AUTH_FAILURE.id,
                        CloseReason.LOGIN_AUTH_FAILURE.desc);
                break;
        }
    }

}

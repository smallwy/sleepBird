package moster.game.platform;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import moster.game.player.PlayerSystem;
import moster.infras.network.CloseReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的平台系统决策。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class PlatformDecision extends ActionDecision {

    /**
     * 当前的决策状态。
     */
    private Status status = Status.SIGN_IN;

    @Autowired
    private PlatformSystem platformSystem;

    @Autowired
    private DecisionSystem decisionSystem;

    @Autowired
    private PlayerSystem playerSystem;

    @Override
    public void tick(long currTime) {
        switch (this.status) {
            case SIGN_IN:
                signIn();
                break;

            case SIGN_UP:
            default:
                signUp();
                break;
        }
    }

    /**
     * 执行平台账号的注册逻辑。
     */
    private void signUp() {
        PlatformComponent platformComponent = super.getOwner().getComponent(PlatformComponent.class);
        int signUpStatus = platformComponent.getSignUpStatus();

        switch (signUpStatus) {
            case -1:
                // 账号注册失败。
                this.playerSystem.playerLogout(super.owner, true, CloseReason.LOGIN_AUTH_FAILURE.id, CloseReason.LOGIN_AUTH_FAILURE.desc);
                break;

            case 0:
                // 开始注册账号。
                platformComponent.setSignUpStatus(2);
                platformSystem.signUp(platformComponent);
                break;

            case 1:
                // 账号注册成功后，需要重新走一次账号登入流程。
                this.status = Status.SIGN_IN;
                platformComponent.setSignInStatus(0);
                break;

            case 2:
            default:
                // do nothing
                break;
        }
    }

    /**
     * 执行平台账号的登入逻辑。
     */
    private void signIn() {
        PlatformComponent platformComponent = super.getOwner().getComponent(PlatformComponent.class);
        int signInStatus = platformComponent.getSignInStatus();

        switch (signInStatus) {
            case -1:
                // 登入失败
                this.playerSystem.playerLogout(super.owner, true, CloseReason.LOGIN_AUTH_FAILURE.id, CloseReason.LOGIN_AUTH_FAILURE.desc);
                break;

            case -2:
                // 需要注册
                this.status = Status.SIGN_UP;
                break;

            case 0:
                // 开始登入
                platformComponent.setSignInStatus(2);
                platformSystem.signIn(platformComponent);
                break;

            case 1:
                // 登入认证成功
                this.decisionSystem.switchNext(super.owner);
                break;

            case 2:
            default:
                // do nothing
                break;

        }
    }


    static enum Status {
        /**
         * 处于账号登入中。
         */
        SIGN_IN,

        /**
         * 处于账号注册中。
         */
        SIGN_UP,
        ;
    }

}

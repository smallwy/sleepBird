package moster.game.basic;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionEvent;
import moster.game.decision.DecisionSystem;
import moster.game.decision.event.ErrorCodeEvent;
import moster.game.login.LoginComponent;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 基础信息决策，指导获取玩家的基本信息。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class BasicInfoDecision extends ActionDecision {

    /**
     * 是否正在取名中。
     */
    private boolean nameChanging;

    /**
     * 是否获取了基本信息。
     */
    private boolean haveGetInfo;

    /**
     * 是否更换了自己的头像。
     */
    private boolean haveChangeIcon;

    @Autowired
    private BasicInfoSystem basicInfoSystem;

    @Autowired
    private DecisionSystem decisionSystem;

    @Override
    public void tick(long currTime) {
        BasicInfoComponent basicInfoComponent = super.owner.getComponent(BasicInfoComponent.class);

        // 先检查玩家是否有名字。
        if (StringUtils.isEmpty(basicInfoComponent.playerName)) {
            String account = super.owner.getComponent(LoginComponent.class).getAccount();

            if (this.nameChanging) {
                ActionDecision.logger.debug("玩家[{}]改名等待中", account);
            } else {
                this.nameChanging = true;
                this.basicInfoSystem.changeName(super.owner, account);
            }

            return;
        }

        // 查看自身信息。
        if (this.haveGetInfo) {
            // 玩家无论如何都会有一个头像，如果这里数量为0，说明还没有收到个人信息，需要继续等待个人信息。
            if (basicInfoComponent.headIcons.size() == 0) {
                ActionDecision.logger.debug("玩家[{}]获取个人信息等待中", super.owner);
                return;
            }

        } else {
            this.haveGetInfo = true;
            basicInfoComponent.headIcons.clear();
            basicInfoSystem.getPlayerInfo(super.owner, 0L);
            return;
        }

        // 修改头像
        if (this.haveChangeIcon) {
            if (basicInfoComponent.headIcon == -1) {
                // 玩家当前的头像为-1，说明需要继续等待个人信息。
                return;
            }

        } else {
            // 玩家的当前头像不可能为-1，通过这个值来判断玩家是否收到了个人信息。
            // 玩家在收到个人信息后，会更新当前头像为正确的值。
            basicInfoComponent.headIcon = -1;
            this.haveChangeIcon = true;

            // 随机使用一个头像。
            int headIconAmount = basicInfoComponent.headIcons.size();
            int headIconIndex = RandomUtils.nextInt(0, headIconAmount);
            int newHeadIcon = basicInfoComponent.headIcons.get(headIconIndex);

            // 随机使用一个头像框。
            int headIconFrameAmount = basicInfoComponent.headIconFrames.size();
            int newHeadIconFrame = 0;

            if (headIconFrameAmount > 0) {
                int headIconFrameIndex = RandomUtils.nextInt(0, headIconFrameAmount);
                newHeadIconFrame = basicInfoComponent.headIconFrames.get(headIconFrameIndex);
            }

            this.basicInfoSystem.changeHeadIcon(super.owner, newHeadIcon, newHeadIconFrame);
            return;
        }

        this.decisionSystem.switchNext(super.owner);
    }


    @DecisionEvent
    public void onErrorCode(ErrorCodeEvent event) {
        if (event.errorCode == 10041) {
            ActionDecision.logger.warn("玩家[{}]获取基本信息出错，收到错误码[{}]", super.owner, event.errorCode);
            this.decisionSystem.switchNext(super.owner);
        }
    }

    @Override
    public void onEnter() {
        this.nameChanging = false;
        this.haveGetInfo = false;
        this.haveChangeIcon = false;
    }

}

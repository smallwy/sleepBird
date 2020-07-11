package moster.game.shop;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的商店决策。
 *
 * @author Gene
 */
@Component
@Scope("prototype")
public class ShopDecision extends ActionDecision {

    @Autowired
    private ShopSystem system;

    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * 请求商品列表
     */
    private boolean haveGetInfo;
    /**
     * 请求商品详细信息
     */
    private boolean haveGetDetailedInfo;

    /**
     * 是否已购买
     */
    private boolean buy;
    /**
     * 是否已刷新
     */
    private boolean refresh;

    @Override
    public void tick(long currTime) {
        if (!this.haveGetInfo) {
            this.system.getInfo(this.owner);
            this.haveGetInfo = true;
            return;
        }
        if (!this.haveGetDetailedInfo) {
            this.system.getDetailedInfo(this.owner);
            this.haveGetDetailedInfo = true;
            return;
        }
        if (!this.buy) {
            this.system.buy(this.owner);
            this.buy = true;
            return;
        }
        if (!this.refresh) {
            this.system.refresh(this.owner);
            this.refresh = true;
            return;
        }
        this.decisionSystem.switchNext(super.owner);
    }

    @Override
    public void onEnter() {
        this.haveGetInfo = false;
        this.haveGetDetailedInfo = false;
        this.buy = false;
        this.refresh = false;
    }

}

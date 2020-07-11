package moster.game.arena;

import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionEvent;
import moster.game.decision.DecisionSystem;
import moster.game.decision.event.ErrorCodeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的竞技场决策。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class ArenaDecision extends ActionDecision {

    @Autowired
    private ArenaSystem arenaSystem;

    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * 是否需要刷新(获取)个人竞技场信息。
     */
    private boolean haveGetInfo;

    /**
     * 是否获取了排行榜。
     */
    private boolean haveGetRankList;

    /**
     * 是否查看了战斗记录。
     */
    private boolean haveGetRecord;

    @Override
    public void tick(long currTime) {
        // 获取个人信息。
        if (!this.haveGetInfo) {
            this.arenaSystem.getArenaInfo(super.owner);
            this.haveGetInfo = true;
            return;
        }

        // 查看排行榜。
        ArenaComponent arenaComponent = super.owner.getComponent(ArenaComponent.class);
        if (!haveGetRankList) {
            this.arenaSystem.getScoreRankList(super.owner, arenaComponent.rankCurrPage);
            this.haveGetRankList = true;
            return;

        } else {
            if (arenaComponent.rankCurrPage < arenaComponent.rankTotalPage) {
                this.arenaSystem.getScoreRankList(super.owner, arenaComponent.rankCurrPage + 1);
                return;
            }
        }

        // 查看竞技场战斗记录。
        if (!this.haveGetRecord) {
            this.arenaSystem.getBattleRecords(super.owner);
            this.haveGetRecord = true;
            return;
        }

        // 所有行为都做完了，可以切换到下一个决策。
        this.decisionSystem.switchNext(super.owner);
    }


    @DecisionEvent
    public void onErrorCode(ErrorCodeEvent event) {
        // FIXME zhangfei 暂时不处理竞技场的错误码
    }

    @Override
    public void onEnter() {
        this.haveGetInfo = false;
        this.haveGetRankList = false;
        this.haveGetRecord = false;
    }

}

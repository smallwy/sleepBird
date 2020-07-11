package moster.game.pass;

import com.gameart.dict.DictManager;
import com.gameart.dict.data.item.Currency;
import com.gameart.dict.data.scene.PassType;
import com.gameart.dict.data.scene.ScenePassDict;
import com.gameart.dict.data.scene.ScenePassDictEntry;
import moster.game.basic.BasicInfoComponent;
import moster.game.decision.ActionDecision;
import moster.game.decision.DecisionEvent;
import moster.game.decision.DecisionSystem;
import moster.game.decision.event.ErrorCodeEvent;
import moster.game.decision.event.PassFightEvent;
import moster.game.gm.GmSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 玩家的主线副本关卡决策。
 * 从第一章的第一个关卡开始，一步一步的向上通关。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class StoryPassDecision extends ActionDecision {

    /**
     * 是否获取了某些副本信息。
     */
    private boolean haveGetInfo;

    @Autowired
    private PassSystem passSystem;

    @Autowired
    private GmSystem gmSystem;

    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * 当前正在进行的关卡id。
     */
    private int currPassId;

    /**
     * 是否有挑战的目标关卡。
     */
    private boolean haveTargetPass;

    /**
     * 正在挑战关卡。
     */
    private boolean challenging;

    /**
     * 是否正在等待体力值。
     */
    private boolean waitingPower;

    @Override
    public void tick(long currTime) {
        PassComponent passComponent = super.owner.getComponent(PassComponent.class);

        // 请求获取主线副本的当前章节信息。
        // 这一步获取的信息对后面的战斗不重要，但是也模拟一下。
        if (!this.haveGetInfo) {
            this.haveGetInfo = true;
            passComponent.currInstances.remove(PassConstants.INSTANCE_TYPE_STORY);
            this.passSystem.getChapterInfo(super.owner, PassConstants.INSTANCE_TYPE_STORY);
            return;

        } else {
            if (!passComponent.currInstances.containsKey(PassConstants.INSTANCE_TYPE_STORY)) {
                // 还没有获取到主线副本信息，需要继续等待。
                ActionDecision.logger.debug("玩家[{}]获取副本信息等待中", super.owner);
                return;
            }
        }

        // 如果正在挑战关卡，需要等待挑战响应。
        if (this.challenging) {
            // 关卡战斗返回等待中。
            ActionDecision.logger.debug("玩家[{}]主线副本挑战响应等待中", super.owner);
            return;
        }

        // 更新当前挑战的关卡id。
        if (!this.haveTargetPass) {
            this.haveTargetPass = true;

            if (this.currPassId == 0) {
                this.currPassId = getFirstPassId(101);
            } else {
                this.currPassId = getNextPassId(this.currPassId);
            }

            // 获取挑战的关卡失败，需要切换到其它决策。
            if (this.currPassId == 0) {
                ActionDecision.logger.debug("玩家[{}]没有获取到下一个要挑战的主线副本", super.owner);
                this.decisionSystem.switchNext(super.owner);
                return;
            }
        }

        BasicInfoComponent basicInfoComponent = super.owner.getComponent(BasicInfoComponent.class);
        long currPower = basicInfoComponent.getCurrency(Currency.POWER.getType());
        ScenePassDictEntry passDictEntry = DictManager.getInstance().getDictEntry(ScenePassDictEntry.class, this.currPassId);

        // 检查当前体力值是否可以挑战关卡。
        if (passDictEntry.winDeduct > currPower) {
            if (!this.waitingPower) {
                this.waitingPower = true;

                // 使用GM指令添加体力。
                this.gmSystem.addItem(super.owner, 200004, 100);
                ActionDecision.logger.debug("玩家[{}]挑战主线副本[{}]时体力不足，请求GM指令获取体力", super.owner, this.currPassId);

            } else {
                ActionDecision.logger.debug("玩家[{}]挑战主线副本[{}]时体力不足，等待GM指令添加体力", super.owner, this.currPassId);
            }

            return;
        }

        this.waitingPower = false;
        this.challenging = true;

        ActionDecision.logger.debug("玩家[{}]开始挑战主线副本[{}]", super.owner, this.currPassId);
        this.passSystem.passFight(super.owner, this.currPassId, 0, 0);
    }

    /**
     * 收到关卡战斗响应事件。
     */
    @DecisionEvent
    public void onPassFightEvent(PassFightEvent event) {
        if (event.win) {
            this.challenging = false;
            this.haveTargetPass = false;
        } else {
            ActionDecision.logger.info("玩家[{}]主线副本[{}]挑战失败", super.owner, this.currPassId);
            this.decisionSystem.switchNext(super.owner);
        }
    }

    @DecisionEvent
    public void onErrorCode(ErrorCodeEvent event) {
        if (event.errorCode == 10903) {
            ActionDecision.logger.warn("玩家[{}]主线副本[{}]挑战失败，收到错误码[{}]",
                    super.owner, this.currPassId, event.errorCode);
            this.decisionSystem.switchNext(super.owner);
        }
    }

    /**
     * 获取某个章节的第一个关卡。
     *
     * @param chapterId 章节id
     */
    private int getFirstPassId(int chapterId) {
        ScenePassDict passDict = DictManager.getInstance().getDict(ScenePassDict.class);
        int firstPassId = 0;

        for (ScenePassDictEntry dictEntry : passDict.getAll().values()) {
            if (dictEntry.chapter != chapterId) {
                continue;
            }

            if (firstPassId == 0) {
                firstPassId = dictEntry.idx;
            } else if (firstPassId > dictEntry.idx) {
                firstPassId = dictEntry.idx;
            }
        }

        return firstPassId;
    }

    /**
     * 获取下一个关卡。
     *
     * @param lastPassId 上一个关卡
     */
    private int getNextPassId(int lastPassId) {
        ScenePassDict passDict = DictManager.getInstance().getDict(ScenePassDict.class);
        ScenePassDictEntry lastEntry = passDict.get(lastPassId);
        int maxPassId = getMaxPassId(lastEntry.chapter);

        if (lastPassId >= maxPassId) {
            // 切换到下一章的第一个关卡。
            return getFirstPassId(lastEntry.chapter + 1);

        } else {
            int nextPassId = 0;

            // 找到下一个关卡
            for (ScenePassDictEntry dictEntry : passDict.getAll().values()) {
                if (dictEntry.chapter != lastEntry.chapter) {
                    continue;
                }

                // 找到上一个关卡的下一个关卡。
                if (dictEntry.idx > lastPassId) {
                    if (nextPassId == 0) {
                        nextPassId = dictEntry.idx;
                    } else if (nextPassId > dictEntry.idx) {
                        nextPassId = dictEntry.idx;
                    }
                }
            }

            // 下一个关卡有可能是跳转关卡
            if (nextPassId != 0) {
                ScenePassDictEntry nextEntry = passDict.get(nextPassId);
                if (nextEntry.type == PassType.Skip.id) {
                    nextPassId = getNextPassId(nextPassId);
                }
            }

            return nextPassId;
        }
    }

    /**
     * 获取某个章节最大的关卡id。
     */
    private int getMaxPassId(int chapterId) {
        int maxPassId = 0;
        ScenePassDict passDict = DictManager.getInstance().getDict(ScenePassDict.class);

        for (ScenePassDictEntry dictEntry : passDict.getAll().values()) {
            if ((dictEntry.chapter == chapterId) && (dictEntry.idx > maxPassId)) {
                maxPassId = dictEntry.idx;
            }
        }

        return maxPassId;
    }

    @Override
    public void onEnter() {
        this.waitingPower = false;
        this.haveTargetPass = false;
        this.challenging = false;
        this.currPassId = 0;
    }

}

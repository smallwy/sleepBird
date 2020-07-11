package moster.game.decision;

import moster.game.player.Player;
import moster.infras.util.type.ManagedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 玩家行为决策，用于确定玩家在某一个时刻选择做什么。
 *
 * @author zhangfei
 */
@ManagedType
public abstract class ActionDecision {

    protected static Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 决策的拥有者。
     */
    protected Player owner;

    /**
     * 决策的最大概率值。
     * 随机一个数值，如果这个值在这个范围[minProb, maxProb]内，就选中这个决策。
     */
    protected int maxProb;

    /**
     * 驱动做出决策。
     *
     * @param currTime 当前时间
     */
    public abstract void tick(long currTime);

    /**
     * 进入这个决策时的一些初始化操作。
     */
    public void onEnter() {

    }

    /**
     * 退出这个决策时的一些清理操作。
     */
    public void onExit() {

    }

    public Player getOwner() {
        return this.owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getMaxProb() {
        return maxProb;
    }

    public void setMaxProb(int maxProb) {
        this.maxProb = maxProb;
    }

}

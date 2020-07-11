package moster.game.basic;

import moster.game.player.PlayerComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家的基本信息。
 *
 * @author zhangfei
 */
public class BasicInfoComponent extends PlayerComponent {

    /**
     * 玩家id。
     */
    long playerId;

    /**
     * 玩家名字。
     */
    String playerName;

    /**
     * 头像。
     */
    int headIcon;

    /**
     * 拥有的所有头像。
     */
    List<Integer> headIcons = new ArrayList<>();

    /**
     * 头像框。
     */
    int headIconFrame;

    /**
     * 拥有的所有头像框。
     */
    List<Integer> headIconFrames = new ArrayList<>();

    /**
     * 玩家宣言。
     */
    String statement;

    /**
     * 玩家等级。
     */
    int level;

    /**
     * 玩家战力。
     */
    int fightValue;

    /**
     * 家族id。
     */
    long guildId;

    /**
     * vip等级。
     */
    int vip;

    /**
     * 当前经验值。
     */
    long exp;

    /**
     * 当前称号。
     */
    int title;

    /**
     * 货币。
     */
    List<Long> currencies = new ArrayList<>();

    /**
     * 获取某项货币的数量。
     *
     * @param currencyIndex 货币类型
     * @return 货币数量
     */
    public long getCurrency(int currencyIndex) {
        return this.currencies.get(currencyIndex);
    }

}

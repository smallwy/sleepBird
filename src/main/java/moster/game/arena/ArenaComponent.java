package moster.game.arena;

import moster.game.player.PlayerComponent;

/**
 * 玩家的竞技场组件。
 *
 * @author zhangfei
 */
public class ArenaComponent extends PlayerComponent {

    /**
     * 当前积分。
     */
    int score;

    /**
     * 今天剩余挑战次数。
     */
    int remainTimes;

    /**
     * 今天已购买挑战次数。
     */
    int buyTimes;

    /**
     * 下一次购买挑战次数花费的货币数量。
     */
    int buyCost;

    /**
     * 查看的当前排行榜页数。
     */
    int rankCurrPage;

    /**
     * 总的排行榜页数。
     */
    int rankTotalPage;

}

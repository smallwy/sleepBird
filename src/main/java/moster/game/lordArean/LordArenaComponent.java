package moster.game.lordArean;

import moster.game.player.PlayerComponent;
import com.gameart.proto.message.LoadArenaProto;

import java.util.List;

/**
 * 太上论剑组件。
 *
 * @author stone
 */
public class LordArenaComponent extends PlayerComponent {

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

    /**
     * 可以挑战的选手
     */
    List<LoadArenaProto.LoadArenaTargetInfo> changellgers;

}

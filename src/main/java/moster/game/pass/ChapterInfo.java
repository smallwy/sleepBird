package moster.game.pass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangfei
 */
public class ChapterInfo {

    /**
     * 章节id。
     */
    int chapterId;

    /**
     * 副本类型。
     */
    int mainType;

    /**
     * 章节的所有关卡信息，可以为关卡id，value为对应的关卡信息。
     */
    Map<Integer, PassInfo> passes = new HashMap<>();

    /**
     * 星级宝箱领取状态。
     */
    List<Boolean> starChestDrawStates = new ArrayList<>();

    /**
     * 已经领取闯关赠卡奖励的关卡ID集合。
     */
    private List<Integer> passPresentList = new ArrayList<>();

}

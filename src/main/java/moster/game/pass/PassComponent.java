package moster.game.pass;

import moster.game.player.PlayerComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家关卡组件。
 *
 * @author zhangfei
 */
public class PassComponent extends PlayerComponent {

    /**
     * 玩家的所有章节信息。
     */
    Map<Integer, ChapterInfo> chapters = new HashMap<>();

    /**
     * 当前正在进行的副本信息，key为副本类型，value为当前的章节id。
     */
    Map<Integer, Integer> currInstances = new HashMap<>();

}


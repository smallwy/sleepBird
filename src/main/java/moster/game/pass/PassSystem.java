package moster.game.pass;

import moster.game.decision.DecisionSystem;
import moster.game.decision.event.PassFightEvent;
import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.PassProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhangfei
 */
@Component
public class PassSystem implements ISystem, RegisteredMessageCommand {

    @Autowired
    private DecisionSystem decisionSystem;

    /**
     * 玩家收到关卡信息。
     */
    @InboundMessageCommand(commandId = 10902, desc = "关卡信息")
    public void receivePassInfo(Player player, PassProto.RespPassInfo resp) {
        PassComponent passComponent = player.getComponent(PassComponent.class);

        for (PassProto.ChapterInfo cInfo : resp.getChapterInfosList()) {
            ChapterInfo chapterInfo = new ChapterInfo();
            chapterInfo.chapterId = cInfo.getChapterId();
            chapterInfo.mainType = cInfo.getType();

            // 设置章节的关卡信息。
            for (PassProto.PassInfo pInfo : cInfo.getPassInfosList()) {
                PassInfo passInfo = new PassInfo();
                passInfo.passId = pInfo.getPassId();
                passInfo.passed = pInfo.getPassed();
                passInfo.star = pInfo.getStar();
                passInfo.buyNum = pInfo.getBuyNum();
                passInfo.fightNum = pInfo.getFightNum();
                passInfo.passTime = pInfo.getPassTime();
                chapterInfo.passes.put(passInfo.passId, passInfo);

                // 记录当前正在打的副本类型的章节。
                Integer currChapter = passComponent.currInstances.get(chapterInfo.mainType);
                if (currChapter == null) {
                    passComponent.currInstances.put(chapterInfo.mainType, chapterInfo.chapterId);
                } else {
                    if (chapterInfo.chapterId > currChapter) {
                        passComponent.currInstances.put(chapterInfo.mainType, chapterInfo.chapterId);
                    }
                }
            }

            // 新创建的玩家是没有任何关卡信息的。
            if (!passComponent.currInstances.containsKey(chapterInfo.mainType)) {
                passComponent.currInstances.put(chapterInfo.mainType, 0);
            }

            chapterInfo.starChestDrawStates.addAll(cInfo.getReceivedStarList());
            passComponent.chapters.put(chapterInfo.chapterId, chapterInfo);
        }
    }

    /**
     * 挑战某个关卡。
     */
    public void passFight(Player player, int passId, int teamType, int conditionId) {
        PassProto.ReqPassFight.Builder req = PassProto.ReqPassFight.newBuilder();
        req.setPassId(passId);
        req.setTeamType(teamType);
        req.setContditionId(conditionId);
        player.send(req);
    }

    /**
     * 收到关卡战斗返回。
     */
    @InboundMessageCommand(commandId = 10904, desc = "关卡战斗返回")
    public void receivePassFightInfo(Player player, PassProto.RespPassFight resp) {
        boolean win = resp.getPassInfo().getPassed();
        PassFightEvent event = new PassFightEvent(resp.getPassInfo().getPassId(), win);
        this.decisionSystem.emit(player, event);
    }

    /**
     * 获取某个类型的章节信息。
     *
     * @param mainType 副本类型
     */
    public void getChapterInfo(Player player, int mainType) {
        PassProto.ReqPassInfo.Builder req = PassProto.ReqPassInfo.newBuilder();
        req.setType(mainType);
        player.send(req);
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(PassProto.ReqPassInfo.class, 10901, "获取关卡章节信息");
        RegisteredMessageCommand.registerOutboundMessage(PassProto.ReqPassFight.class, 10903, "关卡战斗");
    }

}

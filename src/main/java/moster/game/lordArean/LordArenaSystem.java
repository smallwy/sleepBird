package moster.game.lordArean;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.BattleProto;
import com.gameart.proto.message.LoadArenaProto;
import com.gameart.utils.RandomUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 论剑系统。
 *
 * @author stone
 */
@Component
public class LordArenaSystem implements ISystem, RegisteredMessageCommand {

    /**
     * 请求获取玩家的竞技场信息。
     */
    public void getArenassInfo(Player player) {
        LoadArenaProto.ReqLoadArenaInfo.Builder req = LoadArenaProto.ReqLoadArenaInfo.newBuilder();
        player.send(req);
    }

    /**
     * 收到个人的竞技场信息。
     */
    @InboundMessageCommand(commandId = 17003, desc = "太上论剑信息")
    public void receiveArenassInfo(Player player, LoadArenaProto.RespLoadArenaInfo resp) {
        LordArenaComponent arenaComponent = player.getComponent(LordArenaComponent.class);
        arenaComponent.score = resp.getSegement();
        arenaComponent.buyTimes = resp.getBuyTimes();
        arenaComponent.buyCost = resp.getBuyCost();
        arenaComponent.remainTimes = resp.getRemainTimes();
        arenaComponent.changellgers = resp.getTargetList();
    }

    /**
     * 请求获取竞技场排行榜。
     */
    public void getScoreRankssList(Player player, int page) {
        LoadArenaProto.ReqLoadArenaScoreRankList.Builder req = LoadArenaProto.ReqLoadArenaScoreRankList.newBuilder();
        req.setPage(page);
        player.send(req);
    }

    /**
     * 收到竞技场排行榜列表信息。
     */
    @InboundMessageCommand(commandId = 17018, desc = "太上论剑积分排行榜")
    public void receiveScoressRankList(Player player, LoadArenaProto.RespLoadArenaScoreRankList resp) {
        LordArenaComponent lordArenaComponent = player.getComponent(LordArenaComponent.class);
        lordArenaComponent.rankCurrPage = resp.getCurrPage();
        lordArenaComponent.rankTotalPage = resp.getTotalPage();
    }

    /**
     * 请求获取玩家的竞技场战斗记录。
     */
    public void getBattlessRecords(Player player) {
        LoadArenaProto.ReqLoadArenaRecords.Builder req = LoadArenaProto.ReqLoadArenaRecords.newBuilder();
        player.send(req);
    }

    /**
     * 收到竞技场战斗记录信息。
     */
    @InboundMessageCommand(commandId = 17013, desc = "查看战斗记录")
    public void receiveBattlessRecords(Player player, LoadArenaProto.RespLoadArenaRecords resp) {

    }


    /**
     * 请求获取玩家的竞技场战斗记录。
     */
    public void getLoadArenassChallenge(Player player) {
        LoadArenaProto.ReqLoadArenaChallenge.Builder req = LoadArenaProto.ReqLoadArenaChallenge.newBuilder();
        LordArenaComponent lordArenaComponent = player.getComponent(LordArenaComponent.class);

        List<LoadArenaProto.LoadArenaTargetInfo> arenaTargetInfos = lordArenaComponent.changellgers;
        long id = 0;
        if (arenaTargetInfos != null && arenaTargetInfos.size() > 0) {
            id = RandomUtil.randomList(arenaTargetInfos, 1).get(0).getId();

        }
        req.setTargetId(id);
        if (id != 0) {
            player.send(req);
        }
    }

    /**
     * 收到竞技场战斗记录信息。
     */
    @InboundMessageCommand(commandId = 17005, desc = "挑战太上论剑对手")
    public void receiveLoadArenassChallenge(Player player, LoadArenaProto.RespLoadArenaChallenge resp) {
        LoadArenaProto.LoadArenaBattleInfo loadArenaBattleInfo = resp.getInfo();
        BattleProto.BattleResult result = loadArenaBattleInfo.getReport();
    }


    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(LoadArenaProto.ReqLoadArenaInfo.class, 17002, "请求太上论剑信息");
        RegisteredMessageCommand.registerOutboundMessage(LoadArenaProto.ReqLoadArenaScoreRankList.class, 17017, "查看太上论剑积分排行榜");
        RegisteredMessageCommand.registerOutboundMessage(LoadArenaProto.ReqLoadArenaRecords.class, 17012, "查看战斗记录");
        RegisteredMessageCommand.registerOutboundMessage(LoadArenaProto.ReqLoadArenaChallenge.class, 17004, "挑战太上论剑对手");
    }

}
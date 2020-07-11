package moster.game.arena;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.ArenaProto;
import org.springframework.stereotype.Component;

/**
 * 竞技场系统。
 *
 * @author zhangfei
 */
@Component
public class ArenaSystem implements ISystem, RegisteredMessageCommand {

    /**
     * 请求获取玩家的竞技场信息。
     */
    public void getArenaInfo(Player player) {
        ArenaProto.ReqArenaInfo.Builder req = ArenaProto.ReqArenaInfo.newBuilder();
        player.send(req);
    }

    /**
     * 收到个人的竞技场信息。
     */
    @InboundMessageCommand(commandId = 10702, desc = "竞技场信息")
    public void receiveArenaInfo(Player player, ArenaProto.RespArenaInfo resp) {
        ArenaComponent arenaComponent = player.getComponent(ArenaComponent.class);
        arenaComponent.score = resp.getScore();
        arenaComponent.buyTimes = resp.getBuyTimes();
        arenaComponent.buyCost =  resp.getBuyCost();
        arenaComponent.remainTimes = resp.getRemainTimes();
    }

    /**
     * 请求获取竞技场排行榜。
     */
    public void getScoreRankList(Player player, int page) {
        ArenaProto.ReqArenaScoreRankList.Builder req = ArenaProto.ReqArenaScoreRankList.newBuilder();
        req.setPage(page);
        player.send(req);
    }

    /**
     * 收到竞技场排行榜列表信息。
     */
    @InboundMessageCommand(commandId = 10718, desc = "竞技场积分排行榜")
    public void receiveScoreRankList(Player player, ArenaProto.RespArenaScoreRankList resp) {
        ArenaComponent arenaComponent = player.getComponent(ArenaComponent.class);
        arenaComponent.rankCurrPage = resp.getCurrPage();
        arenaComponent.rankTotalPage = resp.getTotalPage();
    }

    /**
     * 请求获取玩家的竞技场战斗记录。
     */
    public void getBattleRecords(Player player) {
        ArenaProto.ReqArenaRecords.Builder req = ArenaProto.ReqArenaRecords.newBuilder();
        player.send(req);
    }

    /**
     * 收到竞技场战斗记录信息。
     */
    @InboundMessageCommand(commandId = 10712, desc = "竞技场战斗记录")
    public void receiveBattleRecords(Player player, ArenaProto.RespArenaRecords resp) {

    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(ArenaProto.ReqArenaInfo.class, 10701, "请求竞技场信息");
        RegisteredMessageCommand.registerOutboundMessage(ArenaProto.ReqArenaScoreRankList.class, 10717, "查看竞技场积分排行榜");
        RegisteredMessageCommand.registerOutboundMessage(ArenaProto.ReqArenaRecords.class, 10711, "查看竞技场战斗记录");
    }

}

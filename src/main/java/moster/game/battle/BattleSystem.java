package moster.game.battle;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.BattleProto;
import org.springframework.stereotype.Component;

/**
 * 战斗系统。
 *
 * @author zhangfei
 */
@Component
public class BattleSystem implements ISystem, RegisteredMessageCommand {

    @InboundMessageCommand(commandId = 15002, desc = "战斗镜像")
    public void receiveBattleVideo(Player player, BattleProto.RespBattleVidoe resp) {
        BattleProto.RespBattlePlayState.Builder req = BattleProto.RespBattlePlayState.newBuilder();
        req.setPlayState(10);
        player.send(req);
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(BattleProto.RespBattlePlayState.class, 15003, "战斗结束播放");
    }
}

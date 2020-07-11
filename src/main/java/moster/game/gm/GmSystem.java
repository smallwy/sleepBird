package moster.game.gm;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.GmProto;
import org.springframework.stereotype.Component;

/**
 * @author zhangfei
 */
@Component
public class GmSystem implements ISystem, RegisteredMessageCommand {

    /**
     * 添加道具。
     *
     * @param itemId 道具id
     * @param itemNum 道具数量
     */
    public void addItem(Player player, int itemId, int itemNum) {
        GmProto.ReqGm.Builder req = GmProto.ReqGm.newBuilder();
        req.setModule("storage");
        req.setCommand("addItem " + itemId + " " + itemNum);
        player.send(req);
    }

    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(GmProto.ReqGm.class, 10011, "GM指令");
    }

}

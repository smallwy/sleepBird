package moster.game.HandBook;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.InboundMessageCommand;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.CardProto;
import io.netty.util.internal.ThreadLocalRandom;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 图鉴系统。
 *
 * @author Gene
 */
@Component
public class HandBookSystem implements ISystem, RegisteredMessageCommand {


    /**
     * 请求打开图鉴界面
     */
    public void getInfo(Player player) {
        CardProto.ReqCardOpenHandbook.Builder req = CardProto.ReqCardOpenHandbook.newBuilder();
        req.setSystemId(ThreadLocalRandom.current().nextInt(2) + 1);
        player.send(req);
    }

    /**
     * /返回打开图鉴界面
     */
    @InboundMessageCommand(commandId = 10356)
    public void shopPane(Player player, CardProto.RespCardOpenHandbook resp) {
        List<CardProto.Handbook> cardsList = resp.getItemList();
        List<Integer> exp0 = new ArrayList<>();
        List<Integer> expMore = new ArrayList<>();
        for (CardProto.Handbook handbook : cardsList) {
            if(handbook.getAddExp() > 0){
                expMore.add(handbook.getId());
            }else{
                exp0.add(handbook.getId());
            }
        }
        List<Integer> last = null;
        if(ThreadLocalRandom.current().nextInt(10) > 2){
            last = expMore;
        }else{
            last = exp0;
        }
        if(!last.isEmpty()){
            Integer cardId = last.get(ThreadLocalRandom.current().nextInt(last.size()));
            CardProto.ReqCardUpdateHandbook.Builder req = CardProto.ReqCardUpdateHandbook.newBuilder();
            req.setSystemId(resp.getSystemId());
            req.setId(cardId);
            player.send(req);
        }
    }

    /**
     * 返回更新某张卡牌图鉴。
     */
    @InboundMessageCommand(commandId = 10358)
    public void update(Player player, CardProto.RespCardUpdateHandbook resp) {
        CardProto.ReqCardUpLevHandbook.Builder req = CardProto.ReqCardUpLevHandbook.newBuilder();
        req.setSystemId(resp.getSystemId());
        req.setType(resp.getType());
        player.send(req);
    }

    @Override
    public void registerOutboundMessages() {
//        RegisteredMessageCommand.registerOutboundMessage(CardProto.ReqCardOpenHandbook.class, 10355);
//        RegisteredMessageCommand.registerOutboundMessage(CardProto.ReqCardUpdateHandbook.class, 10357);
//        RegisteredMessageCommand.registerOutboundMessage(CardProto.ReqCardUpLevHandbook.class, 10361);
    }
}

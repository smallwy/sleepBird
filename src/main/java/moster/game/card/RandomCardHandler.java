package moster.game.card;

import moster.game.common.CommonComponent;
import moster.game.common.CommonSystem;
import moster.game.common.MessageType;
import moster.game.common.handler.AbstractHandler;
import moster.game.player.Player;
import com.gameart.proto.message.CardProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RandomCardHandler extends AbstractHandler {


    @Autowired
    CommonSystem commonSystem;


    @Override
    public void execute(Player owner) {
        CardProto.ReqRandCard.Builder builder = CardProto.ReqRandCard.newBuilder();
        builder.setPoolType(1);
        builder.setTimesType(CardProto.ReqRandCard.TimesType.SINGLE);
        commonSystem.send(owner, builder.build());
    }

    @Override
    public void nextFrameExecute(Player owner) {
        CommonComponent component = owner.getComponent(CommonComponent.class);
        CardProto.RespRandCard resp = component.getResp(MessageType.RESP_REAMDOM_CARD);
        System.out.println(resp);
    }
}

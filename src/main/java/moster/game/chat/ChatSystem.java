package moster.game.chat;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.RegisteredMessageCommand;
import com.gameart.proto.message.ChatProto;
import com.gameart.utils.RandomUtil;
import org.springframework.stereotype.Component;

@Component
public class ChatSystem implements ISystem, RegisteredMessageCommand {


    public void sendMsg(Player owner) {

        ChatProto.ReqChatSendMessage.Builder req = ChatProto.ReqChatSendMessage.newBuilder();
        req.setChannel(ChannelType.world);
        req.setTemplateType(1);
        req.setContent("夷陵老祖<color=green>[魏无羡]</color>温馨提示您：代码千万行，注释第一行；编程不规范，同事两行泪！" + RandomUtil.generateBetween(1, 10000));
        owner.send(req);
    }


    @Override
    public void registerOutboundMessages() {
        RegisteredMessageCommand.registerOutboundMessage(ChatProto.ReqChatSendMessage.class, 11202, "发送聊天信息");
    }


}
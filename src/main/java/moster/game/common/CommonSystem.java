package moster.game.common;

import moster.game.player.Player;
import moster.infras.core.ecs.ISystem;
import moster.infras.core.message.MessageManager;
import moster.infras.core.message.RegisteredMessageCommand;
import com.google.protobuf.AbstractMessage;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class CommonSystem implements ISystem, RegisteredMessageCommand {

    public void send(Player player, AbstractMessage message) {
        player.send(message);
    }

    public void receive(Player player, AbstractMessage message) {
        int commandId = MessageManager.getCommandId(message.getClass());
        CommonComponent commonComponent = player.getComponent(CommonComponent.class);
        commonComponent.addResp(commandId, message);
    }

    @Override
    public boolean registerInboundMethod() {
        try {
            Method receive = this.getClass().getMethod("receive", Player.class, AbstractMessage.class);
            for (MessageType type : MessageType.values()) {
                inboundMethods.put(type.getCommandId(), receive);
            }
            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void registerOutboundMessages() {
        for (MessageType type : MessageType.values()) {
//            RegisteredMessageCommand.registerOutboundMessage(type.getMessage(), type.getCommandId());
        }
    }
}

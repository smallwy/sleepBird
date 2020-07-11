package moster.infras.core.message;

import moster.infras.core.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 默认的消息处理器。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class DefaultMessageProcessor implements MessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMessageProcessor.class);

    /**
     * 当前消息处理器的拥有者。
     */
    private Client owner;

    @Override
    public void process(SimpleMessage simpleMessage) {
        CommandHandler handler = MessageManager.getMessageHandler(simpleMessage.commandId);
        if (handler == null) {
            logger.debug("Client[{}]不能处理未知消息[{}]", this.owner, simpleMessage.commandId);
            return;
        }

        Runnable task = () -> {
            handler.process(this.owner, simpleMessage.content);
        };
        this.owner.receive(task);
    }

    public DefaultMessageProcessor(Client client) {
        this.owner = client;
    }

}

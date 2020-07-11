package moster.game.player;

import moster.game.login.LoginComponent;
import moster.infras.core.client.Client;
import moster.infras.core.ecs.BaseEntity;
import moster.infras.core.executor.EventExecutor;
import moster.infras.core.message.MessageProcessor;
import moster.infras.core.message.SimpleMessage;
import moster.infras.core.remote.RemoteEndpoint;
import com.google.protobuf.AbstractMessage;

/**
 * 玩家。
 *
 * @author zhangfei
 */
public class Player extends BaseEntity implements Client {

    /**
     * 远端连接。
     */
    private RemoteEndpoint remoteEndpoint;

    /**
     * 消息处理器。
     */
    private MessageProcessor messageProcessor;

    /**
     * 用于处理发送给当前玩家的事件的执行器。
     */
    private EventExecutor eventExecutor;

    @Override
    public void receive(SimpleMessage simpleMessage) {
        this.messageProcessor.process(simpleMessage);
    }

    @Override
    public void receive(Runnable message) {
        this.eventExecutor.execute(message);
    }

    @Override
    public void send(SimpleMessage simpleMessage) {
        this.remoteEndpoint.send(simpleMessage);
    }

    @Override
    public void send(AbstractMessage protoMessage) {
        this.remoteEndpoint.send(protoMessage);
    }

    @Override
    public void send(AbstractMessage.Builder<?> protoBuilder) {
        this.remoteEndpoint.send(protoBuilder);
    }

    public RemoteEndpoint getRemoteEndpoint() {
        return this.remoteEndpoint;
    }

    public void setRemoteEndpoint(RemoteEndpoint remoteEndpoint) {
        this.remoteEndpoint = remoteEndpoint;
    }

    public MessageProcessor getMessageProcessor() {
        return this.messageProcessor;
    }

    public void setMessageProcessor(MessageProcessor messageProcessor) {
        this.messageProcessor = messageProcessor;
    }

    @Override
    public EventExecutor getEventExecutor() {
        return this.eventExecutor;
    }

    public void setEventExecutor(EventExecutor eventExecutor) {
        this.eventExecutor = eventExecutor;
    }

    @Override
    public String toString() {
        return "player-" + getComponent(LoginComponent.class).getAccount();
    }

}

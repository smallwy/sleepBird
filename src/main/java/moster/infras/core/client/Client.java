package moster.infras.core.client;

import moster.infras.core.actor.GameActor;
import moster.infras.core.executor.EventExecutor;
import moster.infras.core.message.SimpleMessage;
import com.google.protobuf.AbstractMessage;

/**
 * 表示一个具有收发消息功能的远端对象。
 *
 * @author zhangfei
 */
public interface Client extends GameActor {

    /**
     * 接受(处理)一个消息。
     *
     * @param simpleMessage 消息
     */
    void receive(SimpleMessage simpleMessage);

    /**
     * 向远端发送消息。
     *
     * @param simpleMessage 消息对象
     */
    void send(SimpleMessage simpleMessage);

    /**
     * 向远端发送消息。
     *
     * @param protoMessage 消息对象
     */
    void send(AbstractMessage protoMessage);

    /**
     * 向远端发送消息。
     *
     * @param protoBuilder 消息对象
     */
    void send(AbstractMessage.Builder<?> protoBuilder);

    /**
     * 用于执行内部业务逻辑的执行器。
     * 通过给每个client分配一个执行器，显示的将client的业务逻辑处理分配到同一个执行器上。
     *
     * @return 执行器
     */
    EventExecutor getEventExecutor();

}

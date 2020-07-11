package moster.infras.core.remote;

import moster.infras.core.client.Client;
import moster.infras.core.message.MessageManager;
import moster.infras.core.message.SimpleMessage;
import com.google.protobuf.AbstractMessage;

/**
 * 表示与远端建立的连接。
 * 隐藏底层实现细节，向上提供统一的简化接口。
 * 比如上层在使用时，并不关注底层用的是netty的channel，还是用的是mina的session对象。
 *
 * @author zhangfei
 */
public abstract class RemoteEndpoint {

    /**
     * 当前连接的状态。
     */
    protected ConnectState state = ConnectState.CONNECTING;

    /**
     * 向远端发送消息。
     *
     * @param protoBuilder 消息对象
     */
    public void send(AbstractMessage.Builder<?> protoBuilder) {
        AbstractMessage msg = (AbstractMessage) protoBuilder.build();
        send(msg);
    }

    /**
     * 向远端发送消息。
     *
     * @param protoMessage 消息对象
     */
    public void send(AbstractMessage protoMessage) {
        int commandId = MessageManager.getCommandId(protoMessage.getClass());
        byte[] content = protoMessage.toByteArray();
        SimpleMessage simpleMessage = new SimpleMessage(commandId, content);
        send(simpleMessage);
    }

    /**
     * 将连接的拥有者与当前连接进行绑定。
     *
     * @param client 连接的拥有者
     */
    public abstract void bind(Client client);

    /**
     * 向远端发送消息。
     *
     * @param simpleMessage 消息对象
     */
    public abstract void send(SimpleMessage simpleMessage);

    /**
     * 关闭与远端保持的连接。
     *
     * @param reason 关闭原因的标识
     * @param detail 关闭原因的描述
     */
    public abstract void close(int reason, String detail);

    /**
     * 向远端发送消息，然后断开远端连接。
     *
     * @param simpleMessage 消息对象
     * @param reason 关闭原因的标识
     * @param detail 关闭原因的描述
     */
    public abstract void sendThenClose(SimpleMessage simpleMessage, int reason, String detail);

    /**
     * 是否已经与远端连上了。
     *
     * @return 如果是就返回true，否则返回false
     */
    public boolean isConnected() {
        return (this.state == ConnectState.CONNECTED);
    }

    /**
     * 是否已经与远端断开了。
     *
     * @return 如果是就返回true，否则返回false
     */
    public boolean isDisconnected() {
        return (this.state == ConnectState.DISCONNECTED);
    }

    /**
     * 是否正在与远端建立连接。
     *
     * @return 如果是就返回true，否则返回false
     */
    public boolean isConnecting() {
        return (this.state == ConnectState.CONNECTING);
    }

    public ConnectState getState() {
        return this.state;
    }

    public void setState(ConnectState state) {
        this.state = state;
    }


    /**
     * 与远端建立连接的状态。
     */
    public enum ConnectState {

        /**
         * 正在连接。
         */
        CONNECTING,

        /**
         * 连接已经断开。
         */
        DISCONNECTED,

        /**
         * 已经连接上。
         */
        CONNECTED,
    }

}

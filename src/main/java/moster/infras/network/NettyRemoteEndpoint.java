package moster.infras.network;

import moster.infras.core.client.Client;
import moster.infras.core.message.MessageManager;
import moster.infras.core.message.SimpleMessage;
import moster.infras.core.remote.RemoteEndpoint;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 基于netty实现的远端连接。
 *
 * @author zhangfei
 */
@Component
@Scope("prototype")
public class NettyRemoteEndpoint extends RemoteEndpoint {

    public static final AttributeKey<Client> CLIENT_KEY = AttributeKey.valueOf("clientKey");

    public static final AttributeKey<ChannelCloseEvent> CLOSE_EVENT_KEY = AttributeKey.valueOf("closeEventKey");

    private static final Logger logger = LoggerFactory.getLogger("PLAYER");

    /**
     * 与远端client保持的连接。
     * 通过使用这个连接，来向远端发送消息。
     */
    private final Channel channel;

    @Override
    public void bind(Client client) {
        channel.attr(NettyRemoteEndpoint.CLIENT_KEY).set(client);
    }

    @Override
    public void send(SimpleMessage simpleMessage) {
        if (logger.isDebugEnabled()) {
            Client client = this.channel.attr(CLIENT_KEY).get();
            String commandDesc = MessageManager.getCommandDesc(simpleMessage.commandId);
            logger.debug("[{}]发送消息[{}-{}]", client, simpleMessage.commandId, commandDesc);
        }

        this.channel.writeAndFlush(simpleMessage);
    }

    @Override
    public void close(int reason, String detail) {
        ChannelCloseEvent closeEvent = new ChannelCloseEvent(reason, detail);
        this.channel.pipeline().fireUserEventTriggered(closeEvent);
    }

    @Override
    public void sendThenClose(SimpleMessage simpleMessage, int reason, String detail) {
        ChannelFutureListener listener = future -> NettyRemoteEndpoint.this.close(reason, detail);
        this.channel.writeAndFlush(simpleMessage).addListener(listener);
    }

    public NettyRemoteEndpoint(Channel channel) {
        this.channel = channel;
    }

}

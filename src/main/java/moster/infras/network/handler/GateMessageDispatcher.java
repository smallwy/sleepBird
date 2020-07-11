package moster.infras.network.handler;

import moster.infras.core.client.Client;
import moster.infras.core.context.ServerContext;
import moster.infras.core.message.SimpleMessage;
import moster.infras.network.ChannelCloseEvent;
import moster.infras.network.CloseReason;
import moster.infras.network.NettyRemoteEndpoint;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 对来自网关服的消息进行分发处理。
 *
 * @author zhangfei
 */
@ChannelHandler.Sharable
public class GateMessageDispatcher extends SimpleChannelInboundHandler<SimpleMessage> {

    private static final Logger logger = LoggerFactory.getLogger(GateMessageDispatcher.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SimpleMessage msg) throws Exception {
        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();
        if (client == null) {
            logger.warn("与网关服之间的channel{}收到消息[{}]，但是还没有绑定client", ctx.channel(), msg.commandId);
            return;
        }

        client.receive(msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof ChannelCloseEvent) {
            ChannelCloseEvent closeEvent = (ChannelCloseEvent) evt;
            ctx.channel().attr(NettyRemoteEndpoint.CLOSE_EVENT_KEY).set(closeEvent);
            ctx.close();
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();

        // channel没有绑定client，就不需要往下处理。
        if (client == null) {
            logger.info("与网关服之间的[未知]的连接{}被断开", ctx.channel());
            return;
        }

        int closeReasonId;
        String closeReasonDesc;
        ChannelCloseEvent closeEvent = ctx.channel().attr(NettyRemoteEndpoint.CLOSE_EVENT_KEY).get();

        // 对主动断开的连接与被动断开的连接做了区分。
        // 1. 被动断开连接(比如对方主动断开连接、协议内容长度解析出错等)，需要对玩家做一些掉线处理。
        if (closeEvent == null) {
            closeReasonId = CloseReason.REMOTE_ACTIVE.id;
            closeReasonDesc = CloseReason.REMOTE_ACTIVE.desc;

            Runnable offlineEvent = ServerContext.getBean("playerOfflineEvent", client, closeReasonId, closeReasonDesc);
            client.receive(offlineEvent);
        }

        // 2. 主动断开连接(比如系统主动断开连接、禁止登入等)，不需要做处理，因为已经做了处理。
        else {
            ctx.channel().attr(NettyRemoteEndpoint.CLOSE_EVENT_KEY).set(null);
            closeReasonId = closeEvent.reasonId;
            closeReasonDesc = closeEvent.reasonDesc;
        }

        logger.info("与网关服之间[{}]的连接{}被断开，原因是[{}-{}]", client, ctx.channel(), closeReasonId, closeReasonDesc);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 只要连接上出现异常，采取的策略就是断开连接。
        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();

        // channel没有绑定玩家，这时就不需要做进一步处理，直接断开连接。
        if (client == null) {
            logger.error("与网关服之间的[未知]的连接{}出现异常", ctx.channel(), cause);
            ctx.close();
        }

        // channel已经绑定玩家，这时就不能直接断开连接，需要通过事件机制来处理。
        else {
            if ((cause instanceof IOException) || (cause instanceof DecoderException)) {
                logger.error("与网关服之间的{}的连接{}出现异常[{}]", client, ctx.channel(), cause.getMessage());
            } else {
                logger.error("与网关服之间{}的连接{}出现异常", client, ctx.channel(), cause);
            }

            int closeReasonId = CloseReason.CHANNEL_EXCEPTION.id;
            String closeReasonDesc = cause.getMessage();
            Runnable offlineEvent = ServerContext.getBean("playerOfflineEvent", client, closeReasonId, closeReasonDesc);
            ctx.fireUserEventTriggered(offlineEvent);
        }
    }

}

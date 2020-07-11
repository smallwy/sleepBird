package moster.infras.network.handler;

import moster.infras.core.client.Client;
import moster.infras.core.message.SimpleMessage;
import moster.infras.network.ChannelCloseEvent;
import moster.infras.network.CloseReason;
import moster.infras.network.NettyRemoteEndpoint;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对来自登入服的消息进行分发处理。
 *
 * @author zhangfei
 */
@ChannelHandler.Sharable
public class LoginMessageDispatcher extends SimpleChannelInboundHandler<SimpleMessage> {

    private static final Logger logger = LoggerFactory.getLogger(LoginMessageDispatcher.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SimpleMessage msg) throws Exception {
        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();
        if (client == null) {
            logger.warn("与登入服之间的channel({})收到消息({})，但是还没有绑定client", ctx.channel(), msg.commandId);
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
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();
//        if (client != null) {
//            logger.info("与登入服之间的channel({})被断开，绑定client的为{}", ctx.channel(), client);
//        } else {
//            logger.info("与登入服之间的channel({})被断开，但是还没有绑定client", ctx.channel());
//        }

        // channel没有绑定client，就不需要往下处理。
        Client client = ctx.channel().attr(NettyRemoteEndpoint.CLIENT_KEY).get();
        if (client == null) {
            logger.info("与登入服之间的channel({})被断开，但是还没有绑定client", ctx.channel());
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

//            Runnable offlineEvent = ServerContext.getBean("playerOfflineEvent", client, closeReasonId, closeReasonDesc);
//            client.receive(offlineEvent);
        }

        // 2. 主动断开连接(比如系统主动断开连接、禁止登入等)，不需要做处理，因为已经做了处理。
        else {
            ctx.channel().attr(NettyRemoteEndpoint.CLOSE_EVENT_KEY).set(null);
            closeReasonId = closeEvent.reasonId;
            closeReasonDesc = closeEvent.reasonDesc;
        }

        logger.info("[{}]与登入服的连接{}被断开，原因是[{}-{}]", client, ctx.channel(), closeReasonId, closeReasonDesc);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("与登入服之间的channel({})出现了异常", ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}

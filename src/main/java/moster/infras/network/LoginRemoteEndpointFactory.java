package moster.infras.network;

import moster.infras.core.remote.RemoteEndpoint;
import moster.infras.core.remote.RemoteEndpointFactory;
import moster.infras.network.handler.LoginChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * @author zhangfei
 */
@Component
public class LoginRemoteEndpointFactory implements RemoteEndpointFactory {

    private static final int CONNECT_TIMEOUT_MILLS = 30000;

    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("login"));

    private static Logger logger = LoggerFactory.getLogger(LoginRemoteEndpointFactory.class);

    @Override
    public RemoteEndpoint create(String host, int port) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(LoginRemoteEndpointFactory.eventLoopGroup);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, LoginRemoteEndpointFactory.CONNECT_TIMEOUT_MILLS);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(LoginChannelInitializer.INSTANCE);

        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(host, port));
        NettyRemoteEndpoint endpoint = new NettyRemoteEndpoint(channelFuture.channel());

        channelFuture.addListener((GenericFutureListener<ChannelFuture>) future -> {
            if (future.isSuccess()) {
                endpoint.setState(RemoteEndpoint.ConnectState.CONNECTED);
                logger.info("与登入服建立连接[{}]成功", channelFuture.channel());
            } else {
                endpoint.setState(RemoteEndpoint.ConnectState.DISCONNECTED);

                try {
                    channelFuture.get();
                } catch (Exception e) {
                    logger.error("与登入服建立连接{}失败[{}]", channelFuture.channel(), e.getMessage());
                }
            }
        });

        return endpoint;
    }

}


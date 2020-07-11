package moster.infras.network.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 用于初始化client与login-server之间channel。
 *
 * @author zhangfei
 */
@ChannelHandler.Sharable
public class LoginChannelInitializer extends ChannelInitializer<SocketChannel> {

    public static final LoginChannelInitializer INSTANCE = new LoginChannelInitializer();

    private static final SimpleMessageCodec CODEC = new SimpleMessageCodec();

    private static final LoginMessageDispatcher DISPATCHER = new LoginMessageDispatcher();

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("extractor", new LengthFieldBasedFrameDecoder(10240, 6, 4, 0, 0));
        pipeline.addLast("codec", CODEC);
        pipeline.addLast("dispatcher", DISPATCHER);
    }

}

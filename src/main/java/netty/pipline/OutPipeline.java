package netty.pipline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;


//入栈 从pipeline中从上到下
public class OutPipeline {

    static class PipelineA extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出栈处理器A被回调");
            super.write(ctx, msg, promise);
        }
    }

    static class PipelineB extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出栈处理器B被回调");
            super.write(ctx, msg, promise);
        }
    }

    static class PipelineC extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出栈处理器C被回调");
            super.write(ctx, msg, promise);
        }
    }

    @Test
    public void testPipelineOutBound() {
        ChannelInitializer channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new PipelineA());
                ch.pipeline().addLast(new PipelineB());
                ch.pipeline().addLast(new PipelineC());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);
        ByteBuf buffer = Unpooled.buffer();
        buffer.writeInt(1);

        channel.writeOutbound(buffer);
        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package moster.infras.network.handler;

import moster.infras.core.message.SimpleMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.AttributeKey;

import java.util.List;

/**
 * 简单消息的编码解码器。
 *
 * @author zhangfei
 */
@ChannelHandler.Sharable
public class SimpleMessageCodec extends MessageToMessageCodec<ByteBuf, SimpleMessage> {

    public static final AttributeKey<Byte> SEQUENCE_KEY = AttributeKey.valueOf("sequenceKey");

    /**
     * 发送给server的消息，序号必须和上一个发送消息中的序号不同。
     */
    private byte getNextSequence(Channel channel) {
        Byte sequence = channel.attr(SEQUENCE_KEY).get();

        if (sequence == null) {
            sequence = 1;
        }

        else {
            sequence++;
            if (sequence >= Byte.MAX_VALUE) {
                sequence = 1;
            }
        }

        channel.attr(SEQUENCE_KEY).set(sequence);
        return sequence;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, SimpleMessage msg, List<Object> out) throws Exception {
        int nextSequence = getNextSequence(ctx.channel());
        int frameLength = 1 + 2 + 1 + 2 + 4 + msg.content.length;
        ByteBuf byteBuf = ctx.alloc().buffer(frameLength);

        byteBuf.writeByte(0x00);              //类型
        byteBuf.writeShort(0x00);             //hash
        byteBuf.writeByte(nextSequence);      //序列号
        byteBuf.writeShort(msg.commandId);    //协议号
        byteBuf.writeInt(msg.content.length); //消息长度
        byteBuf.writeBytes(msg.content);      //消息内容

        out.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        int type = buf.readByte();                       //类型
        int hash = buf.readShort();                      //hash
        int code = buf.readByte();                       //序列号

        int commandId = buf.readShort();
        int length = buf.readInt();

        byte[] content = new byte[length];
        if (length > 0) {
            buf.readBytes(content);
        }

        SimpleMessage message = new SimpleMessage(commandId, content);
        out.add(message);
    }

    @Override
    public String toString() {
        return "SimpleMessageCodec{}";
    }

    public SimpleMessageCodec() {
    }

}

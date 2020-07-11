package moster.infras.core.message;

/**
 * 简单消息。
 *
 * @author zhangfei
 */
public class SimpleMessage {

    /**
     * 消息号id。
     */
    public final int commandId;

    /**
     * 消息内容。
     */
    public final byte[] content;

    /**
     * 生成一个简单消息实例。
     *
     * @param commandId 消息号id
     * @param content 消息内容
     */
    public SimpleMessage(int commandId, byte[] content) {
        this.commandId = commandId;
        this.content = content;
    }

}

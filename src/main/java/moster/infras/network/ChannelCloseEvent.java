package moster.infras.network;

/**
 * channel主动关闭的事件。
 * 我们想知道channel因为什么原因而被关闭，比如正常退出、消息解码错误、心跳超时、逻辑处理异常等。
 *
 * @author zhangfei
 */
public class ChannelCloseEvent {

    /**
     * 关闭原因的标识。
     */
    public final int reasonId;

    /**
     * 关闭原因的描述。
     */
    public final String reasonDesc;

    /**
     * @param reasonId 关闭原因的标识
     */
    public ChannelCloseEvent(int reasonId) {
        this.reasonId = reasonId;
        this.reasonDesc = "";
    }

    /**
     * @param reasonId   关闭原因的标识
     * @param reasonDesc 关闭原因的描述
     */
    public ChannelCloseEvent(int reasonId, String reasonDesc) {
        this.reasonId = reasonId;
        this.reasonDesc = reasonDesc;
    }

}

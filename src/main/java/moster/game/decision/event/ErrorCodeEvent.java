package moster.game.decision.event;

/**
 * 收到错误码的事件。
 *
 * @author zhangfei
 */
public class ErrorCodeEvent {

    /**
     * 产生错误码的消息id。
     */
    public final int messageId;

    /**
     * 错误码。
     */
    public final int errorCode;

    public ErrorCodeEvent(int messageId, int errorCode) {
        this.messageId = messageId;
        this.errorCode = errorCode;
    }

}

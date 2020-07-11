package moster.infras.core.message;

/**
 * 用于消息的处理。
 *
 * @author zhangfei
 */
public interface MessageProcessor {

    /**
     * 对消息进行处理。
     *
     * @param simpleMessage 被处理的消息
     */
    void process(SimpleMessage simpleMessage);

}

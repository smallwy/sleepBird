package moster.infras.core.actor;

/**
 * @author zhangfei
 */
public interface GameActor {

    /**
     * 接受(或处理)一个消息(或任务、事件)。
     *
     * @param message 任务事件
     */
    void receive(Runnable message);

}

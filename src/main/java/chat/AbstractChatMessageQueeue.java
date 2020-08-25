package chat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * 抽象队列
 * */
public abstract class AbstractChatMessageQueeue {

    private final AtomicBoolean enabled;
    private ChatChannel chatChannel;
    protected Runnable scheduleTaskHandle;
    private ScheduledFuture<?> future;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4, Executors.defaultThreadFactory());

    public AbstractChatMessageQueeue(ChatChannel chatChannel) {
        enabled = new AtomicBoolean(false);
        this.chatChannel = chatChannel;
    }

    public abstract boolean checkContidion(Object orgs);


    /*
     * 开启消息处理
     * */
    public void start() {
        if (enabled.compareAndSet(false, true)) {
            if (scheduleTaskHandle != null) {
                future = executorService.scheduleWithFixedDelay(scheduleTaskHandle, 0, 1000, TimeUnit.MILLISECONDS);
            }
        }
    }


    /*
     * 关闭消息处理队列
     * */
    public void stop() {
        if (enabled.compareAndSet(true, false)) {
            if (future != null) {
                future.cancel(true);
            }
        }
    }


    /*
     * 往队列里面增加一条消息
     * */
    public abstract void putMessageToQueue(ChatDataContract contract);

    /*
     * 执行发送的任务
     * */
    public final void doSendjob(Runnable runnable) {
        try {
            executorService.execute(runnable);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    public final boolean isEnable(){
        return enabled.get();
    }
}
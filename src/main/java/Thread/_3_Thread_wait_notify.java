package Thread;

/**
 * wait
 * <p>
 * wait（）改方法是Object类的方法 public final native void wait(long timeout) throws InterruptedException;
 * <p>
 * wait 必须是在synchronized下使用 并且与notify一起使用 同时也要是同一个对象 否则无效
 * <p>
 * <p>
 * <p>
 * <p>
 * wait和sleep的区别
 * <p>
 * （1）wait是Object类下的方法  使当前兑现释放monitor 释放当前的锁  知道别的线程唤醒当前对象的锁（用notify唤醒）
 * （2）sleep是Thread下的方法 当前休眠时间结束 开始争取CPU的时间片段
 * （3）相同点 就是两者都可以使线程处于阻塞状态
 *
 * @author wy
 * @create 2018-11-27 15:33
 **/
public class _3_Thread_wait_notify {
    Object iect = new Object();

}

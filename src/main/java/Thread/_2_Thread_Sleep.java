package Thread;

import org.junit.Test;

/**
 * API
 *
 * @author wy
 * @create 2018-11-26 16:49
 */
public class _2_Thread_Sleep {

  static class t1 extends Thread {
    @Override
    public void run() {
      long startime = System.currentTimeMillis();
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      long endTime = System.currentTimeMillis();
      System.out.println(endTime - startime);
      for (int i = 1; i < 5; i++) {
        System.out.println("开始执行t1线程");
      }
    }
  }

  /** sleep() 使当前线程进入休眠期 百分百的使线程让出时间片段 yeild() 让出当前的时间片段 可能会出现下进程间的切换 */
  @Test
  public void test1() {
    t1 tt = new t1();
    tt.start();
    Thread.yield();
    System.out.println("主线程执行完毕");
  }
}

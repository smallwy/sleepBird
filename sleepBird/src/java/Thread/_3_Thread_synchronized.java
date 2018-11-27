package Thread;

/**
 * synchrionized
 *
 * @author wy
 * @create 2018-11-27 11:40
 */
public class _3_Thread_synchronized implements Runnable {
  private int i = 0;
  private static final int maxcount = 100;

  private Object object = new Object();

  /*当线程共享一份资源的时候得考虑数据的一致性*/
  /* 以下线程输出的数据存在线程安全  可能存在一个先后才能已经改了数据  而另一个先后才能拿到的是改之前的数据*/

  /**
   * synchronized的使用
   *
   * <p>注意事项(1):用synchronized锁定的对象不能为null.(2)synchronized的作用域不宜过大 多大会导致程序的效率变低
   *
   *(3): synchronized必须锁定的是同一个引用 否则不具有排斥性
   */
  @Override
  public void run() {
    synchronized (object) {
      while (i <= maxcount) {
        System.out.println("当前线程名字" + Thread.currentThread().getName() + "------------" + (i++));
      }
    }
  }

  public static void main(String[] args) {
    final _3_Thread_synchronized run = new _3_Thread_synchronized();
    Thread thread1 = new Thread(run, "窗口1");
    Thread thread2 = new Thread(run, "窗口2");
    Thread thread3 = new Thread(run, "窗口3");
    Thread thread4 = new Thread(run, "窗口4");
    thread1.start();
    thread2.start();
    thread3.start();
    thread4.start();
  }
}

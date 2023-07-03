package Thread.Thread_;

import java.util.ArrayList;
import java.util.List;

/**
 * test
 *
 * @author wy
 * @create 2018-11-28 14:41
 */
public class ThreadPoolTest {
  public static void main(String[] args) {
    // 获取线程池
    IThread_Pool t = ThreadPoolImpl.getThreadPool(20);

    List<Runnable> taskList = new ArrayList<Runnable>();
    for (int i = 0; i < 100; i++) {
      taskList.add(new Task());
    }
    // 执行任务
    t.execute(taskList);
    System.out.println(t);
    // 销毁线程
    t.destroy();
    System.out.println(t);
  }

  static class Task implements Runnable {

    private static volatile int i = 1;

    @Override
    public void run() {
      System.out.println("当前处理的线程:" + Thread.currentThread().getName() + " 执行任务" + (i++) + " 完成");
    }
  }
}

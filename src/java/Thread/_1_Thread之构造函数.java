package Thread;

/**
 * 线程与线程组 很多线程的工作任务是类似或者一致的，这样我们就可以使用ThreadGroup来管理他们,
 *
 * <p>ThreadGroup可以随时的获取在他里面的线程的运行状态
 *
 * @author wy
 * @create 2018-11-26 14:40
 */
public class _1_Thread之构造函数 {

  public static void main(String[] args) {
    Thread t1 = new Thread();

    ThreadGroup threadGroup = new ThreadGroup("线程组");

    ThreadGroup threadGroup1 = t1.getThreadGroup().getParent();

    Thread t2 = new Thread(threadGroup, "t2");

    System.out.println(t1.getName());
    System.out.println(threadGroup.getName());
    System.out.println(threadGroup.getParent());
    System.out.println(threadGroup.getMaxPriority());

    System.out.println("线程活跃的数量" + threadGroup.activeCount());
    System.out.println(threadGroup == t2.getThreadGroup());
    System.out.println(threadGroup == t1.getThreadGroup());
    System.out.println(threadGroup1.getMaxPriority());
    System.out.println("线程组的名称" + threadGroup.getParent().getName());
    System.out.println("线程组的名称" + t2.getThreadGroup().getName());
  }
}

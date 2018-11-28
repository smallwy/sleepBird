package Thread.Thread_手写线程池;

import java.util.List;

/**
 * 手写线程池git
 *
 * @author wy
 * @create 2018-11-28 14:24
 */
public interface IThread_Pool {

  /** 加入任务 */
  void execute(Runnable task);


    /**
     * 加入任务
     */
  void execute(Runnable[] tasks);

    /**
     * 任务lie00 列表
     */
  void execute(List<Runnable> tasks);

  /** 销毁线程 */
  void destroy();
}

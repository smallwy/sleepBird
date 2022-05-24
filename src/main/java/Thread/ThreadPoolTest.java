package Thread;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
     /*   for (int i = 0; i < 300; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }*/
        /*
         * int corePoolSize        1:核心线程数
         * int maximumPoolSize     2:最大线程数
         * long keepAliveTime      3:允许线程不干活时间
         * TimeUnit unit           4: 单位
         * BlockingQueue<Runnable> workQueue  5: 任务存放未来得及执行的任务队列
         * ThreadFactory threadFactory        6:线程工厂
         * RejectedExecutionHandler handler  7:拒绝策略
         *
         * 先创建核心线程   核心线程创建满了  则将任务放入阻塞队列中 阻塞队列也满了 则创建非核心线程  再提交任务 则执行拒绝策略
         * <5 核心线程     >5 非核心线程
         *
         * */
      /*  ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 5000, TimeUnit.MILLISECONDS,);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {

            }
        });*/
        //定时线程池 延迟执行线程池
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        /*  ScheduledFuture<Integer> future = scheduledThreadPoolExecutor.schedule(() -> {
            System.out.println("初始化");
            return 1;
        }, 5000, TimeUnit.MILLISECONDS);*/

        //定时线程池 周期性的执行线程池
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            System.out.println("周期性的执行任务");
        }, 2000, 3000, TimeUnit.MILLISECONDS);
        //ScheduledFuture定时线程池获取定时任务返回值
      /*  scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            System.out.println("执行任务");
        }, 2000, 3000, TimeUnit.MILLISECONDS);*/

        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("周期性111111111的执行任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 2000, 3000, TimeUnit.MILLISECONDS);


    //timer异常会导致线程挂掉 所以线程失效了
    /*  Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer的执行任务");
                throw new RuntimeException("unxpection  error");
            }
        }, 2000, 3000);
        Thread.sleep(5000);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("timer1的执行任务");
            }
        }, 2000, 3000);*/
    }/**/
}

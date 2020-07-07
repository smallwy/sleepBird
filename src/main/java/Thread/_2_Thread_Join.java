package Thread;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import org.junit.Test;

/**
 * join
 *
 * @author wy
 * @create 2018-11-26 20:42
 */
public class _2_Thread_Join {

    public class joinThread extends Thread {
        @Override
        public void run() {
            System.out.println("我是子线程  我先执行");
            this.interrupt();
      /*  try {

        Thread.sleep(2);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }*/
            System.out.println("我是子线程 我执行完毕");
        }
    }

    public class mianThread extends Thread {
        @Override
        public void run() {
            System.out.println("my name is main thread ");
            /* try {
             */
            /* Thread.sleep(2);*/
      /*
      } catch (InterruptedException e) {
        e.printStackTrace();
      }*/
            System.out.println("我是主线程 我执行完毕");
        }
    }

    /**
     * join将调用者线程优先执行
     */
    @Test
    public void test() throws InterruptedException {
        joinThread thread = new joinThread();
        mianThread mains = new mianThread();
    /*   thread.setPriority(5);
    mains.setPriority(10);*/

        System.out.println(thread.getName());
        System.out.println(thread.getThreadGroup().getName());
        // 线程的类加载器
        thread.setContextClassLoader(new ClassLoader());
        System.out.println(thread.getContextClassLoader());
        /*    mains.join();*/

        System.out.println(thread.isInterrupted());
        thread.start();
        mains.start();
    }


    public static void main(String[] args) {
        Thread previousThread = Thread.currentThread();
        for (int i = 1; i <= 10; i++) {
            Thread curThread = new JoinThread(previousThread);
            curThread.start();
            previousThread = curThread;
        }
    }


}


class JoinThread extends Thread {
    private Thread thread;

    public JoinThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        try {
            thread.join();
            System.out.println(thread.getName() + " terminated.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}



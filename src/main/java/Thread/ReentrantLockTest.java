package Thread;

import java.util.concurrent.locks.ReentrantLock;


/*锁的重入*/
public class ReentrantLockTest {

    private static ReentrantLock lock = new ReentrantLock();


    public static void main(String[] args) throws InterruptedException {
      /*  lock.lock();

        try {
            System.out.println("enter main syso");
            m1();
        } finally {
            lock.unlock();
        }*/

        Thread thread1 = new Thread(() -> m1());
        Thread thread2 = new Thread(() -> m1());
        thread1.start();
        thread2.start();
        Thread.sleep(100000);
    }


    public static void m1() {
        lock.lock();

        try {
            System.out.println(Thread.currentThread().getName() + "enter m1 syso");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
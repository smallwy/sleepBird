package Thread;

import java.util.concurrent.locks.ReentrantLock;


/*锁的重入*/
public class ReentrantLockTest {

    private static ReentrantLock lock = new ReentrantLock();



    public static void main(String[] args) {
        lock.lock();

        try {
            System.out.println("enter main syso");
            m1();
        } finally {
            lock.unlock();
        }

    }


    public static void m1() {
        lock.lock();

        try {
            System.out.println("enter m1 syso");
            m2();
        } finally {
            lock.unlock();
        }
    }

    public static void m2() {
        lock.lock();

        try {
            System.out.println("enter m2 syso");
        } finally {
            lock.unlock();
        }
    }
}
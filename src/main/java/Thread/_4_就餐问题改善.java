package Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/*当 所有的哲学家都拿起了右手吃饭时候就出现了死锁问题
 * */
public class _4_就餐问题改善 {

    public static void main(String[] args) throws InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();
        int size = 5;
        int thinkingTime = 1000;
        chopStick[] chopstick = new chopStick[size];
        for (int i = 0; i < size; i++)
            chopstick[i] = new chopStick(i);
        for (int i = 0; i < size; i++) {
            executor.execute(new Philosopher(chopstick[i], chopstick[(i + 1) % size], i, thinkingTime));
        }
        Thread.sleep(4 * 1000);

    }
}


class Philosopher1 extends Thread {
    private ReentrantLock leftLock;
    private ReentrantLock rightLock;
    private int randomTime;

    public Philosopher1(ReentrantLock left, ReentrantLock right, int randomTime) {
        this.leftLock = left;
        this.rightLock = right;
        this.randomTime = randomTime;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(randomTime);
                leftLock.lock();
                try {
                    if (rightLock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                        //获得了右手的筷子 进食一段时间
                        try {
                            Thread.sleep(randomTime);
                        } finally {
                            rightLock.unlock();
                        }
                    }

                } finally {
                    leftLock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


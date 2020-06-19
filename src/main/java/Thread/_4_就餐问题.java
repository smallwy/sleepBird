package Thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*当 所有的哲学家都拿起了右手吃饭时候就出现了死锁问题
 * */
public class _4_就餐问题 {

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

class chopStick {

    private int index;

    private boolean use = false;

    public chopStick(int index, boolean use) {
        super();
        this.index = index;
        this.use = use;
    }

    public chopStick(int index) {
        super();
        this.index = index;
    }
}

class Philosopher extends Thread {
    private chopStick left, right;
    private int index;
    private int randomTime;

    public Philosopher(chopStick left, chopStick right, int index, int randomTime) {
        this.left = left;
        this.right = right;
        this.index = index;
        this.randomTime = randomTime;
    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(randomTime);
                System.out.println("-----------" + this.index);
                synchronized (left) {
                    synchronized (right) {
                        System.out.println("-----------" + this.index);
                        Thread.sleep(randomTime);
                    }
                }

            }
        } catch (InterruptedException e) {

        }
    }
}
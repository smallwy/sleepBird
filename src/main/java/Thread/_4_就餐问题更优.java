package Thread;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*当 所有的哲学家都拿起了右手吃饭时候就出现了死锁问题
 * */
public class _4_就餐问题更优 {

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


class Philosopher2 extends Thread {
    private boolean eating;
    private Philosopher2 left;
    private Philosopher2 right;
    private ReentrantLock table;
    private Condition condition;
    private Random random;


    public Philosopher2 getLeft() {
        return left;
    }

    public void setLeft(Philosopher2 left) {
        this.left = left;
    }

    public Philosopher2 getRight() {
        return right;
    }

    public void setRight(Philosopher2 right) {
        this.right = right;
    }

    public Philosopher2(ReentrantLock table) {
        eating = false;
        this.table = table;
        this.condition = table.newCondition();
        random = new Random();
    }

    public void run() {

        while (true) {


        }
    }
}



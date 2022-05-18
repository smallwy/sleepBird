package concurrent.semphore;

import java.util.concurrent.CountDownLatch;

public class countDownLaunch {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(new person(countDownLatch, "一个人")).start();
        new Thread(new doctor(countDownLatch, "一个医生")).start();
        countDownLatch.await();

    }
}

class doctor extends Thread {

    private CountDownLatch countDownLatch;

    public doctor(CountDownLatch countDownLatch, String name) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("看医生--------------");
        countDownLatch.countDown();
    }
}


class person extends Thread {

    private CountDownLatch countDownLatch;

    public person(CountDownLatch countDownLatch, String name) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("排队买票--------------");
        countDownLatch.countDown();
    }
}

package concurrent.semphore;

import java.util.concurrent.Semaphore;

public class semphoreTest1 {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < 5; i++) {
            new Thread(new Task(semaphore, "semaphore" + i)).start();
        }
    }


    public static class Task extends Thread {
        Semaphore semaphore;

        public Task(Semaphore semaphore, String name) {
            super(name);
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName()+":aquire()  time:"+System.currentTimeMillis());
                Thread.sleep(1000);
                semaphore.release();
                System.out.println(Thread.currentThread().getName()+":aquire()  time:"+System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}



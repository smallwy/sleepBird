package Thread;

import org.junit.Test;

public class _5_Thread_interrupted {




    @Test
    public void test4() {


        /**
         *
         *  当使用interrupted（）方法中断线程调用wait() sleep() join（）就会抛出InterruptedException异常
         *   同时清除中断标记位:thread.isInterrupted()==false
         *   否则thread.isInterrupted()==true
         */

        //sleepThread睡眠1000ms
        final Thread sleepThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        //busyThread一直执行死循环
        Thread busyThread = new Thread() {
            @Override
            public void run() {
                while (true) ;
            }
        };
        sleepThread.start();
        busyThread.start();
        sleepThread.interrupt();
        busyThread.interrupt();
        while (sleepThread.isInterrupted()) ;
        System.out.println("sleepThread isInterrupted: " + sleepThread.isInterrupted());
        System.out.println("busyThread isInterrupted: " + busyThread.isInterrupted());
    }


}
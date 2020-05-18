package Thread;

import java.util.concurrent.atomic.AtomicStampedReference;


/*利用版本号解决ABA问题*/
public class TestAtomicStampRefrence {


    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 111);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start");
        String pre = ref.getReference();
        int stamop = ref.getStamp();
        System.out.println("pre" + pre + "诗悦网络" + stamop);
        other();
        Thread.sleep(1);

        System.out.println("213qeww" + ref.compareAndSet(pre, "C", stamop, stamop + 1));

    }

    private static void other() throws InterruptedException {
        new Thread(() -> {
            int stamop = ref.getStamp();
            System.out.println("213qeww11" + ref.compareAndSet(ref.getReference(), "B", stamop, stamop + 1));
        }).start();
        Thread.sleep((long) 0.5);

        new Thread(() -> {
            int stamop = ref.getStamp();
            System.out.println("213qeww11111" + ref.compareAndSet(ref.getReference(), "A", stamop, stamop + 1));
        }).start();

        Thread.sleep((long) 0.5);
    }
}
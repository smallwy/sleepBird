package JVM.Chapter02;

public class test1 {
    public static void main(String[] args) {
        System.out.println("2122");
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

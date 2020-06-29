package lambda.Exercise;

import org.junit.Test;

import java.awt.event.ActionListener;

public class Test2 {

    /*无参数的*/
    @Test
    public void test2() {
        Runnable runnable = () -> System.out.println("lambda表达式");
        Thread thread = new Thread(runnable);
        thread.start();

    }


    /*有一个参数的*/
    @Test
    public void test3() {
        ActionListener actionListener = event -> System.out.println("button click");

        final String[] array = {"hello", "world"};

    }



}
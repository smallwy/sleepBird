package jvm.day3;

import org.openjdk.jol.info.ClassLayout;

public class JOLSample {
    public static void main(String[] args) {
        ClassLayout object = ClassLayout.parseInstance(new Object());
        System.out.println(object.toPrintable());

        ClassLayout arry = ClassLayout.parseInstance(new int[]{});
        System.out.println(arry.toPrintable());

        ClassLayout arry1 = ClassLayout.parseInstance(new int[]{1,3,4,5});
        System.out.println(arry1.toPrintable());

        ClassLayout a = ClassLayout.parseInstance(new A());
        System.out.println(a.toPrintable());
    }

    public static class A {
        int id;   //4B  // 4                int A.id
        String name;  //    4   java.lang.String A.name
        byte b;    //   1               byte A.b
        Object o;  //   4   java.lang.Object A.o  存的地址指针
    }

    //指针压缩

}

package jvm.day1;

public class TestClassLoad {

    static {
        System.out.println("类加载器加载TestClassLoad类");
    }

    public static void main(String[] args) {
        new A();
        B b = null; //B不会加载 除非B是new出来的对象
    }
}

class A {
    static {
        System.out.println("类加载器加载A类");
    }
}

class B {
    static {
        System.out.println("类加载器加载B类");
    }
}

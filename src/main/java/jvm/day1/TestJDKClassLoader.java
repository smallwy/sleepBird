package jvm.day1;

import com.sun.crypto.provider.DESedeKeyFactory;
import sun.misc.Launcher;

import java.net.URL;

public class TestJDKClassLoader {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());
        System.out.println(TestJDKClassLoader.class.getClassLoader());
        System.out.println(TestJDKClassLoader.class.getClassLoader().getClass().getName());
        System.out.println(DESedeKeyFactory.class.getClassLoader().getClass().getName());

        System.out.println(ClassLoader.getSystemClassLoader());

        URL[] urLs = Launcher.getBootstrapClassPath().getURLs();
        for (URL urL : urLs) {
            System.out.println(urL);
        }

        System.out.println(System.getProperty("java.ext.dirs"));

        System.out.println(System.getProperty("java.class.path"));

    }
}

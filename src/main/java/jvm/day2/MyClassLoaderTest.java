package jvm.day2;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class MyClassLoaderTest extends ClassLoader {

    private String classPath;

    public MyClassLoaderTest(String classPath) {
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = getClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        return defineClass(name, classData, 0, classData.length);
    }

    private byte[] getClassData(String name) {
        String path = classPath + "/" + name + ".class";
        try {
            return readClassData(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] readClassData(String path) throws Exception {
        InputStream is = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len = 0;
        byte[] buf = new byte[1024];
        while ((len = is.read(buf)) != -1) {
            bos.write(buf, 0, len);
        }
        is.close();
        return bos.toByteArray();
    }
    public static void main(String[] args) {
        System.out.println(1L << 0);
        System.out.println(2L << 0);
        System.out.println(3L << 0);
        System.out.println(4L << 0);
    }
}


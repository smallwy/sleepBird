package annotion_注解;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @author wy
 * @create 2018-12-27 17:24
 **/
public class test_解析注解 {

    @Test
    public void test1(){
        Method[] methods=  PasswordUtil.class.getDeclaredMethods();
        for(Method method:methods){
      System.out.println(method.getName());
      System.out.println(
      method.getDeclaredAnnotations());
        }
    }
}



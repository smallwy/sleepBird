package annotion_注解;

import java.lang.annotation.*;

/**
 * @author wy
 * @create 2018-12-27 17:20
 **/


@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface annotion_定义 {
    int  id() default 0;
    String name() default "默认值";
}

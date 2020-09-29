package gameart.config.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置文件中key---value的注解，用于读取key 的value的值 alias :字段名称 parser :解析方法名称 <br>
 */
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
	Class<?>[] value() default {};
	String parser() default "";
}

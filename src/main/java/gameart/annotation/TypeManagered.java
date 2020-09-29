 /*****************************
* Copyright 2018 360游戏艺术*
* **************************/
package gameart.annotation;


 import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/****
 * 类型标记
 * 
 * @author JackLei
 * @Date 2018年5月25日 下午2:23:34
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Inherited
@Service
public @interface TypeManagered {

	int index() default 0; // 顺序（从小到大）
}

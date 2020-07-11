package moster.infras.core.message;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注某个方法被用于处理入站消息。
 *
 * @author zhangfei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InboundMessageCommand {

    /**
     * 消息号。
     */
    int commandId();

    /**
     * 消息描述。
     */
    String desc() default "未知";

}

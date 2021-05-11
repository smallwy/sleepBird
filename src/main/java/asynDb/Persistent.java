package asynDb;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Persistent {

    public boolean asyn() default true;

    public Class<? extends Synchronizer> syncClass();
}

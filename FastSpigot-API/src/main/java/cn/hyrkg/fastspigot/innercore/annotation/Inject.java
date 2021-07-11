package cn.hyrkg.fastspigot.innercore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Use this annotation to telling core you want to auto inject a handler to your field.
 * */
public @interface Inject {
    String name() default "";

    int index() default 0;
}

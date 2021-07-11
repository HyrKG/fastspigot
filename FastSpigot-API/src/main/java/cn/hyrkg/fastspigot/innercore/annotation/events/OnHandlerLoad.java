package cn.hyrkg.fastspigot.innercore.annotation.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Annotated it into your handler method and it will be called when it disable!
 * */
public @interface OnHandlerLoad {
}

package cn.hyrkg.fastspigot.spigot.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FastCommand {
    String index() default "";

    String desc();

    //是否需要OP权限
    boolean requireOp() default false;

    //阐述参数的类型
    String[] paramsName() default {};

    //阐述指令排序
    int order() default 0;
}

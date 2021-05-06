package cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//字段注释
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TableField {
    String type(); //字段类型

    IndexType[] indexType() default IndexType.NONE;//字段索引类型

    boolean Nullable() default true; //是否可以为NULL
}

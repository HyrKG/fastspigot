package cn.hyrkg.fastspigot.innercore.annotation;

import cn.hyrkg.fastspigot.innercore.framework.interfaces.IImplementation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
/**
 * Telling core that your service need a implementation instance.<p>
 *
 * Simply annotate it into your custom service interface, and point to your implementation class!
 * */
public @interface ImplService {

    Class<? extends IImplementation> impClass();
}

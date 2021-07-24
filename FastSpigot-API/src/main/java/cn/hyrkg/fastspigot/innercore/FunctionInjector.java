package cn.hyrkg.fastspigot.innercore;

import cn.hyrkg.fastspigot.innercore.annotation.ImplService;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IImplementation;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * 该类对处理器进行功能注入
 **/
@RequiredArgsConstructor
public class FunctionInjector {
    public final FastInnerCore innerCore;

    /**
     * Map of handler to interface implementations.
     */
    private HashMap<Object, HashMap<Class, Object>> implMap = new HashMap<>();

    /**
     * Inspire function registered.
     */
    private final HashMap<String, BiConsumer<Object, HandlerInfo>> inspireMap = new HashMap<>();

    /**
     * Add your inspire to handle handler.<br>
     * Examples:
     * Add Listener inspire to register listener.
     *
     * @param inspireName the name of your inspiration.
     * @param biConsumer  the inspire operation you will do.
     */
    public void addInspire(String inspireName, BiConsumer<Object, HandlerInfo> biConsumer) {
        this.inspireMap.put(inspireName, biConsumer);
    }

    @SneakyThrows
    /**
     * Inspire A handler
     * 1.该类将会将一个Handler所有的(需要实例化）服务接口进行实例化
     * */
    public void inspireHandler(Object handler, HandlerInfo handlerInfo) {
        //TODO 查看接口实现，并对接实现
        Class clazz = handlerInfo.originClass;

        Set<Class> interfaces = new HashSet<>();

        interfaces.addAll(Arrays.asList(clazz.getInterfaces()));


        //find extends interfaces
        //将服务接口的父类接口也纳入启发范围，以确保对功能的复用
        Class superClazz = clazz;
        while (superClazz.getSuperclass() != null && !superClazz.getSuperclass().equals(Object.class)) {
            superClazz = superClazz.getSuperclass();
            interfaces.addAll(Arrays.asList(superClazz.getInterfaces()));
        }

        //load interfaces
        //如果服务接口是实例接口，则启发实例接口，并接入
        for (Class interfaceClazz : interfaces) {
            if (interfaceClazz.isAnnotationPresent(ImplService.class)) {
                ImplService impService = (ImplService) interfaceClazz.getAnnotation(ImplService.class);
                IImplementation implementation = impService.impClass().newInstance();

                //create implementation
                implementation.handleHandler(handler, handlerInfo);

                //put into map
                if (!implMap.containsKey(handler))
                    implMap.put(handler, new HashMap<>());
                implMap.get(handler).put(interfaceClazz, implementation);
            }
        }
        inspireMap.values().forEach(j -> j.accept(handler, handlerInfo));

    }

    public <T> T getImplementation(Object handler, Class<? extends IServiceProvider> implementationService) {
        if (implMap.containsKey(handler))
            return (T) implMap.get(handler).get(implementationService);
        return null;
    }

}

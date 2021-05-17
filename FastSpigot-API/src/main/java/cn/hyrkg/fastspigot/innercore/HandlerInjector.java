package cn.hyrkg.fastspigot.innercore;

import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.innercore.annotation.Instance;
import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerDisable;
import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerPostInit;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.utils.ReflectHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
/**
 * 该类对处理器声明的变量进行注入处理
 * */
public class HandlerInjector {
    /**
     * Main inner core.
     */
    public final FastInnerCore innerCore;

    @Getter
    /**
     * List of created handlers.
     * */
    private ArrayList<Object> handlers = new ArrayList<>();
    /**
     * Injected class to handler info map.
     */
    private HashMap<Class, HandlerInfo> handlerInfoHashMap = new HashMap<>();

    @Getter
    /**
     * Time spent of handler create and inject.
     * */
    private HashMap<HandlerInfo, Long> handlerInjectCost = new HashMap<>();

    /**
     * Get handler info of injected handler class.
     *
     * @param handlerClass injected handler class.
     */
    public HandlerInfo getHandlerInfo(Class handlerClass) {
        //TODO prevent duplicated handler
        return handlerInfoHashMap.get(handlerClass);
    }


    /**
     * 处理实例
     **/
    public void handleInstance(Object instance, Class rawClass) {
        handleInstance(instance, rawClass, null);
    }

    /**
     * 处理实例，并对包含InjectHandler标签的变量进行动态注入
     **/
    public void handleInstance(Object instance, Class rawClass, HandlerInfo parentInfo) {

        //inject instances
        List<Field> fieldInstanceList = ReflectHelper.findFieldIsAnnotated(rawClass, Instance.class);
        for (Field field : fieldInstanceList) {
            try {

                if (field.getType().equals(rawClass)) {
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers()))
                        field.set(null, instance);
                    else
                        field.set(instance, instance);
                }
            } catch (Exception exception) {
                exception.printStackTrace();

            }
        }

        //create and inject handlers
        List<Field> fieldList = ReflectHelper.findFieldIsAnnotated(rawClass, Inject.class);
        for (Field field : fieldList) {
            try {
                //its not allowed to inject same handler in a handler.
                //for preventing endless loop!
                if (field.getType() == rawClass) {
                    innerCore.getCreator().warm(rawClass.getName() + ">" + field.getName() + " is same handler!");
                    continue;
                }

                long timeBefore = System.currentTimeMillis();

                Inject injectInfo = field.getAnnotation(Inject.class);

                //create handler instance and inject to field value
                Object handler = innerCore.getAsmInjector().createWithInjection(field.getType());
                field.setAccessible(true);
                field.set(instance, handler);

                //generate handler info and save it up
                HandlerInfo info = new HandlerInfo(injectInfo, innerCore, parentInfo, field.getType(), handler.getClass(), handler);
                handlerInfoHashMap.put(handler.getClass(), info);
                if (parentInfo != null)
                    parentInfo.addChildInfo(info);
                handlers.add(handler);

                //inject other handlers of this handler
                handleInstance(handler, field.getType(), info);

                //init and inspire handler
                ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(field.getType(), handler, OnHandlerInit.class);
                innerCore.getFunctionInjector().inspireHandler(handler, info);
                ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(field.getType(), handler, OnHandlerPostInit.class);

                //record time that has been spent for logging
                long timeCost = System.currentTimeMillis() - timeBefore;
                handlerInjectCost.put(info, timeCost);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Call when disable,don't try to call it yourself!
     */
    public void onDisable() {
        handlers.forEach(j -> {
            ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(getHandlerInfo(j.getClass()).originClass, j, OnHandlerDisable.class);
        });
    }
}

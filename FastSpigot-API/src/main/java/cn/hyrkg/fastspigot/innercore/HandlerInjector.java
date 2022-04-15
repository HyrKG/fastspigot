package cn.hyrkg.fastspigot.innercore;

import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.innercore.annotation.Instance;
import cn.hyrkg.fastspigot.innercore.annotation.events.*;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.utils.ReflectHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
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
    private HashMap<Object, HandlerInfo> handlerInfoMap = new HashMap<>();

    /**
     * Injected class to handler info map.
     */
    // remove#210725 - Illogical code
    //    private HashMap<Class, HandlerInfo> handlerInfoHashMap = new HashMap<>();

    @Getter
    /**
     * Time spent of handler create and inject.
     * */
    private HashMap<HandlerInfo, Long> handlerInjectCost = new HashMap<>();

    /**
     * Get handler info of injected handler object.
     *
     * @param object injected handler.
     */
    public HandlerInfo getHandlerInfo(Object object) {
        //TODO prevent duplicated handler
        return handlerInfoMap.get(object);
    }


    /**
     * 处理实例，并对包含InjectHandler标签的变量进行动态注入
     **/
    public void initInstance(Object instance, Class rawClass, HandlerInfo parentInfo) {

        //找到Instance标签，并将自身注入
        //注意！通常不推荐使用静态变量加以Instance标签，这将会引发意外的问题。
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

        //1.找到所有Inject标签的变量
        //find all fields annotation by @Inject
        List<Field> fieldList = ReflectHelper.findFieldIsAnnotated(rawClass, Inject.class);

        //2.根据配置中的index进行排序，确保需要按序执行的情况
        //sort by index
        Collections.sort(fieldList, (left, right) -> {
            Inject injectInfoLeft = left.getAnnotation(Inject.class);
            Inject injectInfoRight = right.getAnnotation(Inject.class);

            return ((Integer) injectInfoLeft.index()).compareTo(injectInfoRight.index());
        });


        //3.遍历所有@Inject标签的变量，进行注入
        //foreach all fields then create and inject
        for (Field field : fieldList) {
            try {
                //its not allowed to inject same handler in a handler.
                //for preventing endless loop!
                //为避免死循环，不允许注入自我！
                if (field.getType() == rawClass) {
                    innerCore.getCreator().warm(rawClass.getName() + ">" + field.getName() + " is same handler!");
                    continue;
                }

                long timeBefore = System.currentTimeMillis();

                Inject injectInfo = field.getAnnotation(Inject.class);

                //create handler instance and inject to field value
                //1.实例化变量类，并将值设置在变量上。
                //#注意，ASM在这一步骤运作，是本框架核心。
                Object handler = innerCore.getAsmInjector().createWithInjection(field.getType());
                field.setAccessible(true);
                field.set(instance, handler);

                //generate handler info and save it up
                //2.生成该处理器的信息，并绑定到处理器/上级处理器上
                HandlerInfo info = new HandlerInfo(injectInfo, innerCore, parentInfo, field.getType(), handler.getClass(), handler);
//                handlerInfoHashMap.put(handler.getClass(), info);
                if (parentInfo != null)
                    parentInfo.addChildInfo(info);
                handlerInfoMap.put(handler, info);

                ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(field.getType(), handler, OnHandlerPreInit.class);

                //inject other handlers of this handler
                //3.对该处理器内的，其他处理器进行初始化
                initInstance(handler, field.getType(), info);

                //init and inspire handler
                ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(field.getType(), handler, OnHandlerInit.class);
                //4.对处理进行启发，即对服务接口进行初始化
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
     * 该步骤将会对所有的处理器进行加载
     */
    public void loadInstance(HandlerInfo sourceInfo) {
        long timeBefore = System.currentTimeMillis();
        if (sourceInfo.object != null) {
            ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(sourceInfo.originClass, sourceInfo.object, OnHandlerLoad.class);
        }
        for (HandlerInfo childInfo : sourceInfo.getChildInfo()) {
            loadInstance(childInfo);
        }
        long timeCost = System.currentTimeMillis() - timeBefore;
        if (!handlerInjectCost.containsKey(sourceInfo))
            handlerInjectCost.put(sourceInfo, 0l);
        handlerInjectCost.put(sourceInfo, handlerInjectCost.get(sourceInfo) + timeCost);
    }


    /**
     * Call when disable,don't try to call it yourself!
     */
    public void onDisable() {
        handlerInfoMap.forEach((left, right) -> {
            ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(right.originClass, left, OnHandlerDisable.class);
        });
//        handlers.forEach(j -> {
//            ReflectHelper.findAndInvokeMethodIsAnnotatedSupered(getHandlerInfo(j.getClass()).originClass, j, OnHandlerDisable.class);
//        });
    }
}

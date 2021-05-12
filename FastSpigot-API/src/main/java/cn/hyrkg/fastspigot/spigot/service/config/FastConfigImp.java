package cn.hyrkg.fastspigot.spigot.service.config;

import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IImplementation;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class FastConfigImp implements IImplementation<IFastYamlConfig> {
    private IFastYamlConfig handlerInstance;
    private HandlerInfo handlerInfo;

    @Override
    public void handleHandler(IFastYamlConfig handlerInstance, HandlerInfo handlerInfo) {
        this.handlerInstance = handlerInstance;
        this.handlerInfo = handlerInfo;
        parserFields(handlerInstance, handlerInfo);
    }

    private void parserFields(IFastYamlConfig object, HandlerInfo handlerInfo) {
        ConfigurationSection section = object.getConfigSection();
        if (section == null) {
            handlerInfo.innerCore.getCreator().error(handlerInfo.originClass.getSimpleName() + " parser fields was skipped because null section!");
            return;
        }

        ArrayList<Class> clazzIncludeSuper = new ArrayList<>();
        clazzIncludeSuper.add(handlerInfo.originClass);
        Class superClazz = handlerInfo.originClass;
        while (superClazz.getSuperclass() != null && !superClazz.equals(Object.class)) {
            superClazz = superClazz.getSuperclass();
            clazzIncludeSuper.add(superClazz);
        }

        for (Class clazz : clazzIncludeSuper)
            for (Field field : clazz.getDeclaredFields()) {
                try {
                    if (field.isAnnotationPresent(AutoLoad.class)) {
                        AutoLoad autoLoadInfo = field.getAnnotation(AutoLoad.class);
                        String path = autoLoadInfo.path();
                        String name = autoLoadInfo.name().isEmpty() ? field.getName() : autoLoadInfo.name();
                        String combinePath = (path.isEmpty() ? "" : path + ".") + name;
                        combinePath = combinePath.trim();

                        field.setAccessible(true);
                        if (section.contains(combinePath)) {
                            field.setAccessible(true);
                            if (Modifier.isStatic(field.getModifiers()))
                                field.set(null, readInfo(combinePath, section, field));
                            else
                                field.set(object, readInfo(combinePath, section, field));
                        }
                    }
                } catch (ErrorAutoloadException autoloadException) {
                    object.error("读取配置错误: " + autoloadException.errorMessage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    object.error("发生了错误: " + exception.getMessage());
                }
            }
    }

    private Object readInfo(String path, ConfigurationSection configurationSection, Field field) {
        Class type = field.getType();
        if (type.equals(String.class))
            return configurationSection.getString(path);
        else if (type.equals(double.class) || type.equals(Double.class))
            return configurationSection.getDouble(path);
        else if (type.equals(int.class) || type.equals(Integer.class))
            return configurationSection.getInt(path);
        else if (type.equals(ArrayList.class) || type.equals(List.class)) {
            ArrayList<String> list = new ArrayList<>();
            list.addAll(configurationSection.getStringList(path));
            return list;
        }
        throw new ErrorAutoloadException("Wrong autoload type " + field.getType());
    }

    public void reload() {
        parserFields(handlerInstance, handlerInfo);
    }
}

package cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.core;

import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import cn.hyrkg.fastspigot.spigot.service.simplemysql.orm.po.FastPO;
import lombok.SneakyThrows;

import java.util.HashMap;

public class ORMHandler implements ILoggerService {

    private HashMap<Class, InjectedClassInfo> injectedClasses = new HashMap<>();

    public void registerPO(Class<? extends FastPO> clazz) {
        //TODO inject class
    }


    @SneakyThrows
    public <T> T createPO(Class<? extends T> clazz) {
        if (!injectedClasses.containsKey(clazz))
            return null;
        return (T) injectedClasses.get(clazz).injectedClass.newInstance();
    }

    public <T> void createTable(Class<? extends T> clazz) {
        if (!injectedClasses.containsKey(clazz))
            return;

    }
}

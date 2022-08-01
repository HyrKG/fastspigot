package cn.hyrkg.fastspigot.spigot;

import cn.hyrkg.fastspigot.innercore.FastInnerCore;
import cn.hyrkg.fastspigot.innercore.ICoreCreator;
import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import cn.hyrkg.fastspigot.spigotplugin.support.locker.MysqlLockerManager;
import cn.hyrkg.fastspigot.spigotplugin.support.redis.RedisManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;

public class FastPlugin extends JavaPlugin implements ICoreCreator, ILoggerService {

    public static final int TIME_COST_LIST_AMOUNT = 5;

    @Getter
    private FastInnerCore innerCore;//主要的核心

    private HandlerInfo thisInfo;

    @Inject
    @Getter
    private DebugHandler debugHandler;

    @Override
    public void onEnable() {
        //pre init
        thisInfo = new HandlerInfo(new Inject() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return Inject.class;
            }

            @Override
            public String name() {
                return getPluginName();
            }

            @Override
            public String[] dependsOn() {
                return new String[0];
            }

            @Override
            public int index() {
                return 0;
            }
        }, innerCore, null, getClass(), getClass(), this);
        innerCore = new FastInnerCore(this);

        //init
        long timeStart = System.currentTimeMillis();
        warm("正在加载插件...");
        innerCore.getFunctionInjector().addInspire("ListenerInspire", (j, k) -> {
            if (j instanceof Listener)
                getServer().getPluginManager().registerEvents((Listener) j, this);
        });

        long injectBefore = System.currentTimeMillis();
        warm("注入处理器中...");
        innerCore.getHandlerInjector().initInstance(this, FastPlugin.class, thisInfo);
        innerCore.getHandlerInjector().initInstance(this, getClass(), thisInfo);
        innerCore.getHandlerInjector().loadInstance(thisInfo);
        ArrayList<HandlerInfo> handlerInfos = new ArrayList<>();

        //print time cost
        handlerInfos.addAll(innerCore.getHandlerInjector().getHandlerInjectCost().keySet());
        Collections.sort(handlerInfos,
                (j, k) -> innerCore.getHandlerInjector().getHandlerInjectCost().get(k).compareTo(innerCore.getHandlerInjector().getHandlerInjectCost().get(j)));
        int length = handlerInfos.size() > TIME_COST_LIST_AMOUNT ? TIME_COST_LIST_AMOUNT : handlerInfos.size();
        for (int i = 0; i < length; i++) {
            HandlerInfo info = handlerInfos.get(i);
            warm(ChatColor.RESET + " - " + info.getShortClassPath() + " > " + innerCore.getHandlerInjector().getHandlerInjectCost().get(info) + "ms");
        }
        if (handlerInfos.size() > TIME_COST_LIST_AMOUNT) {
            warm(ChatColor.RESET + " ......");
        }

        warm("注入结束,共注入了" + innerCore.getHandlerInjector().getHandlerInfoMap().size() + "个处理器! (" + (System.currentTimeMillis() - injectBefore) + "ms)");

        warm("加载完毕! (" + (System.currentTimeMillis() - timeStart) + "ms)");

    }


    @Override
    public void onDisable() {
        long timeStart = System.currentTimeMillis();

        warm("正在卸载处理器...");
        innerCore.disable();
        warm("卸载完毕! (" + (System.currentTimeMillis() - timeStart) + "ms)");
    }

    public FastInnerCore getInnerCore() {
        return innerCore;
    }

    public HandlerInfo getHandlerInfo() {
        return thisInfo;
    }

    public String getPluginName() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean isDebugging(Class info) {
        return debugHandler.isDebugging(info);
    }

    @Override
    public boolean checkDependency(String name) {
        if (name.equalsIgnoreCase("fastspigot-redis")) {
            return RedisManager.isEnable();
        } else if (name.equalsIgnoreCase("fastspigot-locker")) {
            return MysqlLockerManager.isEnabled();
        }
        return getServer().getPluginManager().getPlugin(name) != null;
    }
}

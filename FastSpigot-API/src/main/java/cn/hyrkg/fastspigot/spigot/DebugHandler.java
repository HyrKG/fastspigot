package cn.hyrkg.fastspigot.spigot;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import cn.hyrkg.fastspigot.spigot.service.IPluginProvider;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DebugHandler implements IPluginProvider, ILoggerService {

    private boolean globalDebug = false;
    @Getter
    private File debugFile = null;

    private ArrayList<String> debugList = new ArrayList<>();
    private ArrayList<String> lastDebugList = null;
    private HashMap<Class, Boolean> resultCacheMap = new HashMap<>();

    @OnHandlerInit
    @SneakyThrows
    public void onInit() {
        //reset all
        globalDebug = false;
        debugList.clear();

        //init config file
        if (!getPlugin().getDataFolder().exists())
            getPlugin().getDataFolder().mkdirs();

        File debugFile = new File(getPlugin().getDataFolder(), "debug.yml");
        if (!debugFile.exists()) {
            debugFile.createNewFile();
            FileConfiguration defCfg = YamlConfiguration.loadConfiguration(debugFile);
            defCfg.set("debug", new ArrayList<>());
            defCfg.save(debugFile);
        }

        //load config
        FileConfiguration config = YamlConfiguration.loadConfiguration(debugFile);
        if (!config.contains("debug"))
            config.set("debug", new ArrayList<>());

        debugList.clear();
        if (config.isBoolean("debug"))
            globalDebug = config.getBoolean("debug");
        if (config.isList("debug")) {
            debugList.addAll(config.getStringList("debug"));
        }

        //read debug list

        //if contains '*' means allow all
        if (debugList.contains("*"))
            globalDebug = true;

        //done
        if (globalDebug)
            warm("You enabled global debug!");
        else if (!debugList.isEmpty())
            info("" + debugList.size() + " DEBUG configuration(s) has been loaded!");
        else
            info("DEBUG is not enabled!");
    }

    public boolean isDebugging(Class clazz) {
        if (globalDebug)
            return true;
        if (lastDebugList == null || !lastDebugList.equals(debugList)) {
            resultCacheMap.clear();
            lastDebugList = debugList;
        }

        if (resultCacheMap.containsKey(clazz))
            return resultCacheMap.get(clazz);

        boolean result = false;

        HandlerInfo handlerInfo = getInnerCore().getHandlerInjector().getHandlerInfo(clazz);
        if (handlerInfo == null)
            return debugList.contains(clazz.getSimpleName());
        else {
            HandlerInfo[] pathInfo = handlerInfo.getHandlerPath();
            for (int i = 0; i < pathInfo.length; i++) {
                String combine = "";
                for (int k = 0; k < i + 1; k++) {
                    combine += pathInfo[k].originClass.getSimpleName() + ".";
                }
                if (debugList.contains(combine + "*"))
                    result = true;
                combine = combine.trim();
                combine = combine.substring(0, combine.length() - 1);
                if (debugList.contains(combine))
                    result = true;
            }
        }
        resultCacheMap.put(clazz, result);
        return result;
    }

    public void reload() {
        onInit();
    }
}

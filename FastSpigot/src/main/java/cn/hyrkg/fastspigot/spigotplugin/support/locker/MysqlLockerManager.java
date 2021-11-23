package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import cn.hyrkg.fastspigot.spigot.utils.ConfigHelper;
import cn.hyrkg.fastspigot.spigot.utils.FileUtils;
import cn.hyrkg.fastspigot.spigot.utils.MsgHelper;
import cn.hyrkg.fastspigot.spigotplugin.PluginFastSpigot;
import lombok.Getter;
import me.kg.fast.inject.mysql3_1.SimpleMysqlPool;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MysqlLockerManager {

    @Getter
    private static boolean loaded = false;

    @Getter
    private static int expirationTime = 15;

    private static File configFile;
    @Getter
    private static SimpleMysqlPool simpleMysqlPool = null;

    @Getter
    private static ExecutorService executorService = null;
    @Getter
    private static HashMap<String, MysqlLocker> lockers = new HashMap<>();

    public static MysqlLocker getOrCreateLocker(String tableName) {
        if (!lockers.containsKey(tableName)) {
            MysqlLocker locker = new MysqlLocker(tableName);
            locker.initTable();
            lockers.put(tableName, locker);
        }
        return lockers.get(tableName);
    }

    public static void init(JavaPlugin plugin) {
        if (loaded) {
            //prevent load twice
            return;
        }

        executorService = Executors.newCachedThreadPool();

        configFile = new File(plugin.getDataFolder(), "mysqllocker.yml");
        reload(plugin);

        loaded = true;
    }

    public static void reload(JavaPlugin plugin) {
        if (!configFile.exists()) {
            FileUtils.saveResources(PluginFastSpigot.getInstance(), configFile, true);
        }

        //load config
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        ConfigHelper configHelper = ConfigHelper.of(cfg);

        boolean enable = configHelper.key("enable").ofBool(false);
        expirationTime = configHelper.key("expiration").ofInt(15);
        //load if is enabled
        String url = null, user = null, password = null;
        if (enable) {
            url = configHelper.key("mysql.url").ofStr();
            user = configHelper.key("mysql.user").ofStr();
            password = configHelper.key("mysql.password").ofStr();
        }

        lockers.clear();
        //init pool
        if (simpleMysqlPool != null) {
            simpleMysqlPool.closePool();
            simpleMysqlPool = null;
        }

        try {
            //enable
            if (enable) {
                simpleMysqlPool = SimpleMysqlPool.init(10);
                simpleMysqlPool.connect(url, user, password);
            }
        } catch (Exception exception) {
            simpleMysqlPool = null;
            exception.printStackTrace();
            MsgHelper.to(plugin.getServer().getConsoleSender()).warm("加载MYSQL时发生错误!");
        }

        MsgHelper.to(plugin.getServer().getConsoleSender()).warm("MysqlLocker State: " + isEnabled());
    }

    public static boolean isEnabled() {
        return simpleMysqlPool != null;
    }

}

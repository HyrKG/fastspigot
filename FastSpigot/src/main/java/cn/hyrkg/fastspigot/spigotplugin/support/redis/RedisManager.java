package cn.hyrkg.fastspigot.spigotplugin.support.redis;

import cn.hyrkg.fastspigot.spigot.utils.ConfigHelper;
import cn.hyrkg.fastspigot.spigot.utils.FileUtils;
import cn.hyrkg.fastspigot.spigot.utils.MsgHelper;
import cn.hyrkg.fastspigot.spigotplugin.PluginFastSpigot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;

@Getter
public class RedisManager {
    private static File configFile;
    @Setter
    @Getter
    private static String host = "localhost";
    @Setter
    private static String password = "";
    @Setter
    private static int port = 6379, poolIdleSize = 10, poolMaxSize = 1000, timeOutMS = 5000;
    private static boolean enable = true;
    @Getter
    private static int serverPort;

    private static JedisPool jedisPool = null;

    public static void init(JavaPlugin plugin) {
        enable = false;

        serverPort = plugin.getServer().getPort();
        configFile = new File(plugin.getDataFolder(), "redis.yml");
        reload();
        MsgHelper.to(plugin.getServer().getConsoleSender()).warm("Redis State: " + isEnable());
    }

    public static void reload() {
        if (!configFile.exists()) {
            FileUtils.saveResources(PluginFastSpigot.getInstance(), configFile, true);
        }

        //load config
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
        ConfigHelper configHelper = ConfigHelper.of(cfg);

        enable = configHelper.key("enable").ofBool(false);
        host = configHelper.key("redis.host").ofStr("localhost");
        port = configHelper.key("redis.port").ofInt(6379);
        password = configHelper.key("redis.password").ofStr("");
        poolIdleSize = configHelper.key("pool_idle_size").ofInt(10);
        poolMaxSize = configHelper.key("pool_max_size").ofInt(100);
        timeOutMS = configHelper.key("time_out_millisecond").ofInt(5000);

        if (jedisPool != null) {
            jedisPool.close();
            jedisPool = null;
        }


        if (enable) {
            try {
                getJedis().close(); //test connection
            } catch (Exception e) {
                e.printStackTrace();
                enable = false;
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ERROR WHEN TEST REDIS SERVER!");
            }
        }
    }


    /**
     * Get a jedis connection from lazy-load pool
     */
    public static synchronized Jedis getJedis() {
        if (!isEnable())
            return null;
        if (jedisPool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxIdle(poolMaxSize);
            jedisPoolConfig.setMaxIdle(poolIdleSize);
            jedisPoolConfig.setTestOnBorrow(true);
            if (!password.isEmpty())
                jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOutMS, password);
            else
                jedisPool = new JedisPool(jedisPoolConfig, host, port, timeOutMS);
        }
        return jedisPool.getResource();
    }

    public static Jedis getNewJedis() {
        if (!isEnable())
            return null;
        Jedis jedis = new Jedis(host, port);
        if (!password.isEmpty())
            jedis.auth(password);
        return jedis;
    }

    public static boolean isEnable() {
        return enable;
    }
}

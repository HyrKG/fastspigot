package cn.hyrkg.fastspigot.spigot.service.simplemysql.instances;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import cn.hyrkg.fastspigot.spigot.service.config.AutoLoad;
import cn.hyrkg.fastspigot.spigot.service.config.FastConfigImp;
import cn.hyrkg.fastspigot.spigot.service.config.IFastYamlConfig;
import cn.hyrkg.fastspigot.spigot.service.simplemysql.ISimpleMysql;
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.kg.fast.inject.mysql3_1.SimpleMysqlPool;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@RequiredArgsConstructor
public class FastMysqlHandler implements ISimpleMysql, IFastYamlConfig, ILoggerService {

    public final String poolName;

    @AutoLoad
    protected String url = "localhost:3306/yourDatabase";
    @AutoLoad
    protected String user = "root";
    @AutoLoad
    protected String pwd = "";
    @AutoLoad
    protected String table = "defaultTable";

    private ConfigurationSection configurationSection;
    @Getter
    protected SimpleMysqlPool pool;

    @OnHandlerInit
    @SneakyThrows
    public void onInit() {
        JavaPlugin plugin = this.getPlugin();

        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();

        File file = new File(plugin.getDataFolder(), "mysql.yml");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        if (!cfg.contains(getMysqlPoolName())) {
            configurationSection = cfg.createSection(getMysqlPoolName());
            configurationSection.set("url", url);
            configurationSection.set("user", user);
            configurationSection.set("pwd", pwd);
            configurationSection.set("table", table);
            cfg.save(file);
        }
        configurationSection = cfg.getConfigurationSection(getMysqlPoolName());

        if (pool != null)
            pool.closePool();

        try {
            pool = SimpleMysqlPool.init(10, configurationSection.getString("url"), configurationSection.getString("user"), configurationSection.getString("pwd"));
        } catch (MySQLSyntaxErrorException e) {
            error("Mysql error in " + getHandlerInfo().originClass.getSimpleName() + ": " + e.getMessage());
        }
    }

    public String getMysqlPoolName() {
        return poolName;
    }

    @Override
    public ConfigurationSection getConfigSection() {
        return configurationSection;
    }

    @Override
    public void saveConfigurationFile(ConfigurationSection section) {
        //TODO to do nothing here
    }

    @Override
    public boolean isAutoGenerateMissingConfig() {
        return false;
    }

    public void reload() {
        onInit();
        ((FastConfigImp) getImplementation(IFastYamlConfig.class)).reload();
    }


    @Override
    public SimpleMysqlPool getPool() {
        return pool;
    }

    public JavaPlugin getPlugin() {
        return (JavaPlugin) this.getInnerCore().getCreator();
    }
}

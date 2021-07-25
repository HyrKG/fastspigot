package cn.hyrkg.fastspigot.spigot.service.config.instances;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import cn.hyrkg.fastspigot.spigot.service.config.IFastYamlConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class FastFileConfig implements IFastYamlConfig, ILoggerService {

    private File file;

    @OnHandlerInit
    public void onFastPluginConfigInit() {
        file = getFile();

    }

    public abstract File getFile();

    @Override
    public ConfigurationSection getConfigSection() {
        return YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void saveConfigurationFile(ConfigurationSection section) {
        if (section instanceof FileConfiguration) {
            try {
                ((FileConfiguration) section).save(file);
            } catch (IOException e) {
                debug("Error occurred while saving auto-generated configurations: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean shouldAutoParser() {
        return true;
    }
}

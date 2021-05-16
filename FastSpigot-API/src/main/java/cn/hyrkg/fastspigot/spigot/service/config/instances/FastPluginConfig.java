package cn.hyrkg.fastspigot.spigot.service.config.instances;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.spigot.service.IPluginProvider;
import cn.hyrkg.fastspigot.spigot.service.config.IFastYamlConfig;
import cn.hyrkg.fastspigot.spigot.utils.ConsumerBuilder;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

public abstract class FastPluginConfig implements IFastYamlConfig, IPluginProvider {


    @OnHandlerInit
    public void onFastPluginConfigInit() {
        // save resource file if it does not exist
        Path pathConfig = new File(getPlugin().getDataFolder(), "config.yml").toPath();
        if (!Files.exists(pathConfig)) {
            try {
                getPlugin().saveDefaultConfig();
            } catch (IllegalArgumentException e) {
                error("It seems there are not config.yml in plugin resources.");
                error("plugin will create it as a empty file!");

                //create parent directory makes sure can create file
                ConsumerBuilder.of(pathConfig.toFile().getParentFile()).accept(j -> {
                    if (!j.exists() && j.isDirectory())
                        j.mkdirs();
                });

                //create empty file
                try {
                    Files.createFile(pathConfig);
                } catch (IOException ioException) {
                    error("IOException occurred while creating config.yml: " + ioException.getMessage());
                }
            }
        }
    }


    @Override
    public void reloadConfigurations() {
        getPlugin().reloadConfig();
        IFastYamlConfig.super.reloadConfigurations();
    }

    @Override
    public ConfigurationSection getConfigSection() {
        return getPlugin().getConfig();
    }

    @Override
    public void saveConfigurationFile(ConfigurationSection configurationSection) {
        getPlugin().saveConfig();
    }
}

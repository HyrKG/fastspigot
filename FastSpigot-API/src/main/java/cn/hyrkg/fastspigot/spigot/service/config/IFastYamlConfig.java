package cn.hyrkg.fastspigot.spigot.service.config;

import cn.hyrkg.fastspigot.innercore.annotation.ImpService;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import org.bukkit.configuration.ConfigurationSection;

@ImpService(impClass = FastConfigImp.class)
public interface IFastYamlConfig extends IServiceProvider, ILoggerService {

    /**
     * Re-load field's values.
     * Ignore it if you don't have any other configuration which you need to load it yourself!
     */
    default void reloadConfigurations() {
        ((FastConfigImp) getImplementation(IFastYamlConfig.class)).reload();
    }

    /**
     * Returns true means auto generate missing configurations into section
     */
    default boolean isAutoGenerateMissingConfig() {
        return true;
    }

    /**
     * Get configuration section for loading configurations.
     * It is related to autoload service and make sure it is valid!
     */
    ConfigurationSection getConfigSection();

    /**
     * Save configuration file if parser generated default configurations.
     * If you don't wanna generate default configurations automatically just forget it!
     *
     * @param section the section which have new configuration already.
     **/
    void saveConfigurationFile(ConfigurationSection section);


}

package cn.hyrkg.fastspigot.spigot.service;

import cn.hyrkg.fastspigot.innercore.framework.interfaces.IServiceProvider;
import cn.hyrkg.fastspigot.spigot.FastPlugin;

public interface IPluginProvider extends IServiceProvider {
    default FastPlugin getPlugin() {
        return (FastPlugin) getInnerCore().getCreator();
    }
}

package cn.hyrkg.fastspigot.innercore;

import cn.hyrkg.fastspigot.spigot.service.ILogger;

public interface ICoreCreator extends ILogger {
    boolean isDebugging(Class info);

    boolean checkDependency(String name);
}

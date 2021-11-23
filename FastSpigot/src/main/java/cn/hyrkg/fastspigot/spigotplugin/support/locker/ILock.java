package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import java.util.concurrent.FutureTask;

public interface ILock {
    boolean isLocked();

    void lock();

    void unlock();

    boolean hasData();

    void addWaitingExecutor(Runnable runnable);

    FutureTask addLockExecutor(Runnable runnable);
}

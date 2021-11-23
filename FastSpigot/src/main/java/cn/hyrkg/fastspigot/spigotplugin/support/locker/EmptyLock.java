package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import java.util.concurrent.FutureTask;

public class EmptyLock implements ILock {

    public static final EmptyLock EMPTY_LOCK = new EmptyLock();


    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void lock() {

    }

    @Override
    public void unlock() {

    }

    @Override
    public boolean hasData() {
        return false;
    }

    @Override
    public void addWaitingExecutor(Runnable runnable) {
        runnable.run();
    }

    @Override
    public FutureTask addLockExecutor(Runnable runnable) {
        FutureTask futureTask = new FutureTask<>(() -> {
            runnable.run();
            return null;
        });
        futureTask.run();
        return futureTask;
    }
}

package cn.hyrkg.fastspigot.spigot.service.timer;

import cn.hyrkg.fastspigot.innercore.annotation.ImplService;
import cn.hyrkg.fastspigot.spigot.service.IPluginProvider;
import org.bukkit.scheduler.BukkitRunnable;

@ImplService(impClass = BukkitTimerImpl.class)
public interface IBukkitTimer extends IPluginProvider {
    void onTick();

    long getTimerPeriod();

    default long getTimerDelay() {
        return 20;
    }

    default boolean isAutoStart() {
        return true;
    }

    default boolean isAsyncTimer() {
        return true;
    }

    default BukkitRunnable getTimer() {
        return ((BukkitTimerImpl) getImplementation(IBukkitTimer.class)).getTimer();
    }
}

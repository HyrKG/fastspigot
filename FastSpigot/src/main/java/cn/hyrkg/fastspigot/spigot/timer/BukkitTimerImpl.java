package cn.hyrkg.fastspigot.spigot.timer;

import cn.hyrkg.fastspigot.innercore.framework.HandlerInfo;
import cn.hyrkg.fastspigot.innercore.framework.interfaces.IImplementation;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BukkitTimerImpl implements IImplementation<IBukkitTimer> {
    @Getter
    public BukkitRunnable timer = null;

    @Override
    public void handleHandler(IBukkitTimer handlerInstance, HandlerInfo handlerInfo) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                handlerInstance.onTick();
            }
        };
        if (handlerInstance.isAutoStart()) {
            if (handlerInstance.isAsyncTimer())
                Bukkit.getScheduler().runTaskTimerAsynchronously(handlerInstance.getPlugin(), handlerInstance::onTick, handlerInstance.getTimerDelay(), handlerInstance.getTimerPeriod());
            else
                Bukkit.getScheduler().runTaskTimer(handlerInstance.getPlugin(), handlerInstance::onTick, handlerInstance.getTimerDelay(), handlerInstance.getTimerPeriod());
        }
    }
}

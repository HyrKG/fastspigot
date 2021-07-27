package cn.hyrkg.fastspigot.spigot.template.handler;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerDisable;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public abstract class BasePlayerDataCacheHandler<T> implements Listener {

    protected HashMap<Player, T> cacheMap = new HashMap<>();

    public T getData(Player player) {
        Preconditions.checkNotNull(player);
        if (!cacheMap.containsKey(player))
            cacheMap.put(player, createData(player));
        return cacheMap.get(player);
    }

    public abstract T createData(Player player);

    public abstract void onDeleteData(T t);


    @OnHandlerDisable
    public void onBasicPlayerDataCacheHandlerDisable() {
        new HashMap<Player, T>(cacheMap).forEach((j, k) -> {
            onDeleteData(k);
        });
        cacheMap.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (cacheMap.containsKey(event.getPlayer()))
            cacheMap.remove(event.getPlayer());
    }
}

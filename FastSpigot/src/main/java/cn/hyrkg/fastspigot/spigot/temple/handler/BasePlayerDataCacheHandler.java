package cn.hyrkg.fastspigot.spigot.temple.handler;

import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerDisable;
import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public abstract class BasePlayerDataCacheHandler<T> implements Listener {

    protected HashMap<UUID, T> cacheMap = new HashMap<>();

    public T getData(Player player) {
        Preconditions.checkNotNull(player);
        if (!cacheMap.containsKey(player.getUniqueId()))
            cacheMap.put(player.getUniqueId(), createData(player));
        return cacheMap.get(player.getUniqueId());
    }

    public abstract T createData(Player player);

    public abstract void onDeleteData(UUID uuid, T t);


    @OnHandlerDisable
    public void onBasicPlayerDataCacheHandlerDisable() {
        new HashMap<UUID, T>(cacheMap).forEach((j, k) -> {
            onDeleteData(j, k);
        });
        cacheMap.clear();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (cacheMap.containsKey(event.getPlayer().getUniqueId())) {
            onDeleteData(event.getPlayer().getUniqueId(), getData(event.getPlayer()));
            cacheMap.remove(event.getPlayer().getUniqueId());
        }
    }
}

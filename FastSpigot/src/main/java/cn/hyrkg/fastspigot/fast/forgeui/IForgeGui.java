package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public interface IForgeGui {
    void onUpdate();

    void onClose();

    default void onClose(Player viewer) {
        onClose();
    }


    void onMessage(JsonObject jsonObject);

    default void onMessage(Player viewer, JsonObject jsonObject) {
        onMessage(jsonObject);
    }

    UUID getUuid();

    String getGuiShortName();

    SharedProperty getSharedProperty();

    void markDisplayed();

    default Player getViewer() {
        return null;
    }

    default Set<Player> getViewers() {
        HashSet set = new HashSet();
        set.add(getViewer());
        return set;
    }
}

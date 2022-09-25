package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IForgeGui {
    void onUpdate();

    void onClose();

    void onMessage(JsonObject jsonObject);

    UUID getUuid();

    Player getViewer();

    String getGuiShortName();

    SharedProperty getSharedProperty();

    void markDisplayed();
}

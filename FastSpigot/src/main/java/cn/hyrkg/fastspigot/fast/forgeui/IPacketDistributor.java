package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import org.bukkit.entity.Player;

public interface IPacketDistributor {
    JsonObject handle(PacketType packetType, Player player, JsonObject sourceJson);
}

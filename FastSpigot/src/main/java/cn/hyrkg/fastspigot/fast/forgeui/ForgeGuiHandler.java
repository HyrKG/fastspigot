package cn.hyrkg.fastspigot.fast.forgeui;

import cn.hyrkg.fastspigot.fast.forgenet.SimpleModNetwork;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public class ForgeGuiHandler implements PluginMessageListener, Listener {
    public static final String CHANNEL_FORGE_GUI = "fastforgegui";

    private ConcurrentHashMap<Player, BaseForgeGui> viewingForgeGui = new ConcurrentHashMap<>();


    public SimpleModNetwork forgeGuiNetwork;

    private BukkitRunnable updateTimer = null;

    public void init(JavaPlugin plugin, String channelIndex) {
        forgeGuiNetwork = new SimpleModNetwork(channelIndex + "_" + CHANNEL_FORGE_GUI);
        forgeGuiNetwork.init(plugin);
        forgeGuiNetwork.registerCallback(this);

        if (updateTimer != null) {
            updateTimer.cancel();
        }

        updateTimer = new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        };
        updateTimer.runTaskTimerAsynchronously(plugin, 10l, 10l);
    }

    /**
     * 对所有使用中的界面探测更新
     */
    private void update() {
        viewingForgeGui.values().forEach(j -> {
            j.onUpdate();
            if (j.getSharedProperty().detectChange()) {
                updateChanges(j);
            }
        });
    }

    /**
     * 通知客户端更新界面
     */
    public void updateChanges(BaseForgeGui baseForgeGui) {
        JsonObject changes = new JsonObject();
        changes.add("update", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        changes.addProperty("uuid", baseForgeGui.getUuid().toString());
        forgeGuiNetwork.sendPluginMessage(baseForgeGui.getViewer(), changes.toString());
    }

    /**
     * 通知客户端展示界面
     */
    public void display(BaseForgeGui baseForgeGui) {
        if (isPlayerViewing(baseForgeGui.getViewer())) {
            removePlayer(baseForgeGui.getViewer());
        }

        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("gui", baseForgeGui.getGuiShortName());
        displayPacket.add("property", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        forgeGuiNetwork.sendPluginMessage(baseForgeGui.getViewer(), displayPacket.toString());
        viewingForgeGui.put(baseForgeGui.getViewer(), baseForgeGui);
    }

    /**
     * 通知客户端关闭界面
     */
    public void close(BaseForgeGui baseForgeGui) {
        if (isPlayerViewing(baseForgeGui.getViewer())) {
            if (getPlayerViewing(baseForgeGui.getViewer()).getUuid().equals(baseForgeGui.getUuid())) {
                JsonObject displayPacket = new JsonObject();
                displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
                displayPacket.addProperty("close", 0);
                forgeGuiNetwork.sendPluginMessage(baseForgeGui.getViewer(), displayPacket.toString());
                removePlayer(baseForgeGui.getViewer());
            }
        }

    }

    @SneakyThrows
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        String str = new String(bytes, "UTF-8").substring(1);
        JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
        if (jsonObject.has("uuid")) {
            String uid = jsonObject.get("uuid").getAsString();
            if (viewingForgeGui.containsKey(player)) {
                BaseForgeGui baseForgeGui = viewingForgeGui.get(player);
                if (baseForgeGui.getUuid().toString().equalsIgnoreCase(uid)) {
                    if (jsonObject.has("close")) {
                        removePlayer(player);
                    } else if (jsonObject.has("msg")) {
                        baseForgeGui.onMessage(jsonObject.getAsJsonObject("msg"));
                    }
                }

            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    private void removePlayer(Player player) {
        if (isPlayerViewing(player)) {
            viewingForgeGui.get(player).onClose();
            viewingForgeGui.remove(player);
        }
    }

    public boolean isPlayerViewing(Player player) {
        return viewingForgeGui.containsKey(player);
    }

    public BaseForgeGui getPlayerViewing(Player player) {
        return viewingForgeGui.get(player);
    }
}

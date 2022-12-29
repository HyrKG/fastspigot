package cn.hyrkg.fastspigot.fast.forgeui;

import cn.hyrkg.fastspigot.fast.forgenet.SimpleModNetwork;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;

public class ForgeGuiHandler implements PluginMessageListener, Listener {
    public static final String CHANNEL_FORGE_GUI = "ffg";

    private ConcurrentHashMap<Player, IForgeGui> viewingForgeGui = new ConcurrentHashMap<>();

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
     * 向客户端发送消息
     */
    public void sendMessage(SimpleMsg msg, IForgeGui baseForgeGui) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uuid", baseForgeGui.getUuid().toString());
        jsonObject.add("msg", msg.getJsonObj());

        for (Player viewer : baseForgeGui.getViewers()) {
            forgeGuiNetwork.sendPluginMessage(viewer, jsonObject.toString());

        }
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
    public void updateChanges(IForgeGui baseForgeGui) {
        JsonObject changes = new JsonObject();
        changes.add("update", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        changes.addProperty("uuid", baseForgeGui.getUuid().toString());
        for (Player viewer : baseForgeGui.getViewers()) {
            forgeGuiNetwork.sendPluginMessage(viewer, changes.toString());
        }
    }


    /**
     * 通知客户端展示界面
     */
    public void display(IForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("gui", baseForgeGui.getGuiShortName());
        displayPacket.add("property", baseForgeGui.getSharedProperty().generateAndClearUpdate());

        for (Player viewer : baseForgeGui.getViewers()) {
            if (isPlayerViewing(viewer)) {
                removePlayer(viewer);
            }
            forgeGuiNetwork.sendPluginMessage(viewer, displayPacket.toString());
            viewingForgeGui.put(viewer, baseForgeGui);
            baseForgeGui.markDisplayed();
        }
    }

    /**
     * 通知客户端关闭界面
     */
    public void close(BaseForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("close", 0);
        forgeGuiNetwork.sendPluginMessage(baseForgeGui.getViewer(), displayPacket.toString());

        for (Player viewer : baseForgeGui.getViewers()) {
            if (!isPlayerViewing(viewer)) {
                continue;
            }
            if (!getPlayerViewing(viewer).getUuid().equals(baseForgeGui.getUuid())) {
                continue;
            }
            removePlayer(viewer);
        }

    }

    @SneakyThrows
    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        String str = new String(bytes, "UTF-8").substring(1);
        JsonObject jsonObject = new JsonParser().parse(str).getAsJsonObject();
        if (!jsonObject.has("uuid")) {
            return;
        }
        String uid = jsonObject.get("uuid").getAsString();

        if (!isPlayerViewing(player)) {
            return;
        }

        IForgeGui baseForgeGui = getPlayerViewing(player);

        if (!baseForgeGui.getUuid().toString().equalsIgnoreCase(uid)) {
            return;
        }

        if (jsonObject.has("msg")) {
            baseForgeGui.onMessage(player, jsonObject.getAsJsonObject("msg"));
        } else if (jsonObject.has("close")) {
            removePlayer(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    public void removePlayer(Player player) {
        if (isPlayerViewing(player)) {
            IForgeGui gui = viewingForgeGui.get(player);
            gui.onClose(player);
            viewingForgeGui.remove(player);
        }
    }

    public boolean isPlayerViewing(Player player) {
        return viewingForgeGui.containsKey(player);
    }

    public IForgeGui getPlayerViewing(Player player) {
        return viewingForgeGui.get(player);
    }

    public boolean isViewing(IForgeGui gui) {
        return viewingForgeGui.containsValue(gui);
    }
}

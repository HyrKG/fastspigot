package cn.hyrkg.fastspigot.fast.forgeui;

import cn.hyrkg.fastspigot.fast.easygui.EasyGuiHandler;
import cn.hyrkg.fastspigot.fast.forgenet.SimpleModNetwork;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.util.internal.ConcurrentSet;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ForgeGuiHandler implements PluginMessageListener, Listener {
    public static final String CHANNEL_FORGE_GUI = "ffg";

    private HashMap<Player, IForgeGui> viewingForgeGui = new HashMap<>();
    private ConcurrentSet<IForgeGui> guiSet = new ConcurrentSet<>();

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
            String packetStr = jsonObject.toString();
            if (baseForgeGui.getDistributor() != null) {
                JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.message, viewer, jsonObject);
                packetStr = packet.toString();
            }
            forgeGuiNetwork.sendPluginMessage(viewer, packetStr);

        }
    }

    /**
     * 对所有使用中的界面探测更新
     */
    private void update() {
        for (IForgeGui gui : guiSet) {
            gui.onUpdate();
            if (gui.getSharedProperty().detectChange()) {
                updateChanges(gui);
            }
        }
    }

    /**
     * 通知客户端更新界面
     */
    public void updateChanges(IForgeGui baseForgeGui) {
        JsonObject changes = new JsonObject();
        changes.add("update", baseForgeGui.getSharedProperty().generateAndClearUpdate());
        changes.addProperty("uuid", baseForgeGui.getUuid().toString());
        for (Player viewer : baseForgeGui.getViewers()) {
            String packetStr = changes.toString();
            if (baseForgeGui.getDistributor() != null) {
                JsonObject packet = baseForgeGui.getDistributor().handle(PacketType.update, viewer, changes);
                packetStr = packet.toString();
            }
            forgeGuiNetwork.sendPluginMessage(viewer, packetStr);
        }
    }


    public JsonObject generateDisplayPacket(IForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("gui", baseForgeGui.getGuiShortName());
        displayPacket.add("property", baseForgeGui.getSharedProperty().generateCompleteJsonAndClearUpdate());
        return displayPacket;
    }

    /**
     * 通知客户端展示界面
     */
    public void display(IForgeGui baseForgeGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            display(viewer, baseForgeGui, packet,true);
        }
    }

    public void display(IForgeGui baseForgeGui, boolean checkAndCloseOtherGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            display(viewer, baseForgeGui, packet,checkAndCloseOtherGui);
        }
    }

    public void display(Player player, IForgeGui baseForgeGui) {
        JsonObject packet = generateDisplayPacket(baseForgeGui);
        display(player, baseForgeGui, packet,true);
    }

    public void display(Player player, IForgeGui baseForgeGui, JsonObject packet, boolean checkAndCloseOtherGui) {
        if (isPlayerViewing(player)) {
            removePlayer(player);
        }
        if (checkAndCloseOtherGui) {
            EasyGuiHandler.checkAndCloseAll(player);
        }

        String packetStr = packet.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject newPacket = baseForgeGui.getDistributor().handle(PacketType.display, player, packet);
            packetStr = newPacket.toString();
        }

        forgeGuiNetwork.sendPluginMessage(player, packetStr);
        viewingForgeGui.put(player, baseForgeGui);
        guiSet.add(baseForgeGui);
        baseForgeGui.markDisplayed();
    }

    public JsonObject generateClosePacket(IForgeGui baseForgeGui) {
        JsonObject displayPacket = new JsonObject();
        displayPacket.addProperty("uuid", baseForgeGui.getUuid().toString());
        displayPacket.addProperty("close", 0);
        return displayPacket;
    }

    /**
     * 通知客户端关闭界面
     */
    public void close(IForgeGui baseForgeGui) {
        JsonObject packet = generateClosePacket(baseForgeGui);
        for (Player viewer : new ArrayList<>(baseForgeGui.getViewers())) {
            close(viewer, baseForgeGui, packet);
        }
    }

    public void close(Player player, IForgeGui baseForgeGui) {
        JsonObject packet = generateClosePacket(baseForgeGui);
        close(player, baseForgeGui, packet);
    }

    public void close(Player player, IForgeGui baseForgeGui, JsonObject packet) {

        String packetStr = packet.toString();
        if (baseForgeGui.getDistributor() != null) {
            JsonObject newPacket = baseForgeGui.getDistributor().handle(PacketType.close, player, packet);
            packetStr = newPacket.toString();
        }


        forgeGuiNetwork.sendPluginMessage(player, packetStr);
        if (!isPlayerViewing(player)) {
            return;
        }
        if (!getPlayerViewing(player).getUuid().equals(baseForgeGui.getUuid())) {
            return;
        }
        removePlayer(player);
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
            if (!viewingForgeGui.containsValue(gui)) {
                guiSet.remove(gui);
            }

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

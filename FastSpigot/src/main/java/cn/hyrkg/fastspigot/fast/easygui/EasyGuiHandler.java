package cn.hyrkg.fastspigot.fast.easygui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class EasyGuiHandler implements Listener {
    private static Plugin plugin;
    private static EasyGuiHandler instance = null;

    private static CopyOnWriteArrayList<EasyGui> guis = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<TickGui> tickGuis = new CopyOnWriteArrayList<>();


    private EasyGuiHandler() {

    }

    public static void init(Plugin plugin) {
        EasyGuiHandler.plugin = plugin;
        if (instance == null) {
            plugin.getServer().getPluginManager().registerEvents(instance = new EasyGuiHandler(), plugin);
            plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    Iterator<TickGui> guis = tickGuis.iterator();
                    TickGui gui;
                    while (guis.hasNext()) {
                        gui = guis.next();
                        gui.tick();
                    }
                }
            }, 0, 1L);
        }
    }

    public static void registerGui(EasyGui gui) {
        checkAndCloseAll(gui.getViewer());
        guis.add(gui);
        if (gui instanceof TickGui) {
            tickGuis.add((TickGui) gui);
        }
    }

    public static void destoryGui(EasyGui gui) {
        guis.remove(gui);
        if (gui instanceof TickGui)
            tickGuis.remove(gui);
    }

    public static void checkAndCloseAll(Player player) {
        if (isViewing(player)) {
            player.closeInventory();
        }
    }

    public static boolean isViewing(Player player) {
        return getViewing(player) != null;
    }

    public static EasyGui getViewing(Player player) {
        for (EasyGui gui : guis) {
            if (gui.isInv(player.getInventory())) {
                return gui;
            }
            if (gui.getViewer() != null && gui.getViewer().equals(player)) {
                return gui;
            }
        }
        return null;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;
        for (EasyGui gui : guis) {
            if (gui.isInv(e.getInventory())) {
                if (gui.getViewer() == null || gui.getViewer().equals((Player) e.getWhoClicked()))
                    gui.onEvent(e);
                break;
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;
        for (EasyGui gui : guis) {
            if (gui.isInv(e.getInventory())) {
                if (gui.getViewer() == null || gui.getViewer().equals((Player) e.getWhoClicked()))
                    gui.onDragEvent(e);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;
        for (EasyGui gui : guis) {
            if (gui.isInv(e.getInventory())) {
                gui.onClose(e);
                if (gui instanceof TickGui)
                    tickGuis.remove(gui);
                guis.remove(gui);
                break;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        checkAndCloseAll(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(InventoryOpenEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;
        for (EasyGui gui : guis)
            if (gui.isInv(e.getInventory())) {
                if (gui.getViewer() == null || gui.getViewer().equals((Player) e.getPlayer()))
                    gui.onOpen(e);
                break;
            }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent e) {
        ArrayList<EasyGui> clones = new ArrayList<>();
        clones.addAll(guis);

        for (EasyGui gui : clones) {
            gui.onForceClose();
            try {
                gui.onClose(null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            guis.remove(gui);
            if (gui.getViewer() != null)
                gui.getViewer().closeInventory();
        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}

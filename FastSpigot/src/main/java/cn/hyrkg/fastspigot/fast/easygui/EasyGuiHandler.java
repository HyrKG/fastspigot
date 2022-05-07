package cn.hyrkg.fastspigot.fast.easygui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;

public class EasyGuiHandler implements Listener {
    private static EasyGuiHandler instance = null;

    private static ArrayList<EasyGui> guis = new ArrayList<>();
    private static ArrayList<TickGui> tickGuis = new ArrayList<>();


    private EasyGuiHandler() {

    }

    public static void init(Plugin plugin) {
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
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player))
            return;

        for (EasyGui gui : guis)
            if (gui.isInv(e.getInventory())) {
                if (gui.getViewer() != null) {
                    gui.onClose(e);
                    if (gui instanceof TickGui)
                        tickGuis.remove(gui);
                    guis.remove(gui);
                }
                break;
            }

    }

    @EventHandler
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
            guis.remove(gui);
            if (gui.getViewer() != null)
                gui.getViewer().closeInventory();
        }
    }

}

package cn.hyrkg.fastspigot.fast.easygui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public abstract class EasyGui {
    protected Inventory inv = null;
    private boolean verifyClick = true;
    private Player viewer;

    @Deprecated
    public Inventory createInventory() {
        return null;
    }

    public void createInventory(int size, String title) {
        this.inv = Bukkit.createInventory(null, size, title);
    }

    public EasyGui(Player p) {

        this.viewer = p;
        createInventory(9, "$default_inv");
    }

    public Inventory getInv() {
        return inv;
    }

    public Player getViewer() {
        return viewer;
    }

    public boolean isInv(Inventory compareInv) {
        return inv.equals(compareInv);
    }

    public void display() {
        if (inv != null) {
            EasyGuiHandler.registerGui(this);
            viewer.openInventory(inv);
        } else
            viewer.closeInventory();
    }

    public final void close() {
        getViewer().closeInventory();
    }

    public void setVerifyClick(boolean flag) {
        verifyClick = flag;
    }

    public void onEvent(InventoryClickEvent event) {
        if (verifyClick) {
            if (event.getCurrentItem() != null && !event.getCurrentItem().getType().equals(Material.AIR)) {
                if (event.getAction().equals(InventoryAction.NOTHING))
                    return;
                onVerifiedEvent(event);
            }
        } else {
            onVerifiedEvent(event);
        }
    }

    public void onForceClose() {

    }

    public abstract void onVerifiedEvent(InventoryClickEvent event);

    public abstract void onClose(InventoryCloseEvent event);

    public abstract void onOpen(InventoryOpenEvent event);
}

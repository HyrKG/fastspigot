package cn.hyrkg.fastspigot.fast.easygui;

import cn.hyrkg.fastspigot.spigot.utils.MsgHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
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
        Inventory created = createInventory();
        if (created == null)
            this.inv = Bukkit.createInventory(null, size, title);
        else
            this.inv = created;
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
        if (EasyGuiHandler.isViewing(getViewer())) {
            EasyGuiHandler.checkAndCloseAll(getViewer());
            viewer.closeInventory();
            MsgHelper.to(viewer).warm("!! 发生了错误，请重新打开。");
            return;
        }

        if (inv != null) {
            viewer.openInventory(inv);
            EasyGuiHandler.registerGui(this);
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

    public void onDragEvent(InventoryDragEvent event) {

    }

    public abstract void onClose(InventoryCloseEvent event);

    public abstract void onOpen(InventoryOpenEvent event);
}

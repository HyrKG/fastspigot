package cn.hyrkg.fastspigot.fast.forgeui;

import cn.hyrkg.fastspigot.fast.easygui.EasyGui;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.UUID;

public class BaseForgeEasyGui extends EasyGui implements IForgeGui {

    protected final ForgeGuiHandler guiHandler;
    protected final String guiShortName;
    protected UUID uuid = UUID.randomUUID();
    protected SharedProperty sharedProperty = new SharedProperty();
    protected boolean isDisplayed = false;


    public BaseForgeEasyGui(Player p, String guiShortName, ForgeGuiHandler guiHandler) {
        super(p);
        this.guiHandler = guiHandler;
        this.guiShortName = guiShortName;
    }


    @Override
    public void onVerifiedEvent(InventoryClickEvent event) {

    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        guiHandler.close(this);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onMessage(JsonObject jsonObject) {

    }


    @Override
    public void display() {
        super.display();
        this.guiHandler.display(this, false);
        markDisplayed();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getGuiShortName() {
        return guiShortName;
    }

    @Override
    public SharedProperty getSharedProperty() {
        return sharedProperty;
    }

    @Override
    public void markDisplayed() {
        this.isDisplayed = true;
    }

    public SimpleMsg msg() {
        return SimpleMsg.create(this, this.guiHandler);
    }

    public void forceSynProperty() {
        if (!isDisplayed) return;
        if (this.getSharedProperty().detectChange()) {
            guiHandler.updateChanges(this);
        }
    }


}

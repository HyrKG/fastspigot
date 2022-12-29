package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class BaseForgeShareGui implements IForgeGui {
    @Getter
    private UUID uuid = UUID.randomUUID();
    @Getter
    private final String guiShortName;
    @Getter
    private final ForgeGuiHandler guiHandler;

    private HashSet<Player> viewerSets = new HashSet<>();

    protected boolean isDisplayed = false;

    @Getter
    private SharedProperty sharedProperty = new SharedProperty();

    public BaseForgeShareGui(String guiShortName, ForgeGuiHandler guiHandler) {
        this.guiShortName = guiShortName;
        this.guiHandler = guiHandler;

    }

    public void addViewer(Player player) {
        viewerSets.add(player);
    }

    @Override
    public void onUpdate() {

    }


    @Override
    public void onClose(Player viewer) {
        viewerSets.remove(viewer);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onMessage(Player viewer, JsonObject jsonObject) {

    }

    @Override
    public void onMessage(JsonObject jsonObject) {

    }


    public void markDisplayed() {
        isDisplayed = true;
    }


    @Override
    public Set<Player> getViewers() {
        return viewerSets;
    }
    public void forceSynProperty() {
        if (!isDisplayed)
            return;
        if (this.getSharedProperty().detectChange()) {
            guiHandler.updateChanges(this);
        }
    }


    public SimpleMsg msg() {
        return SimpleMsg.create(this, guiHandler);
    }

}

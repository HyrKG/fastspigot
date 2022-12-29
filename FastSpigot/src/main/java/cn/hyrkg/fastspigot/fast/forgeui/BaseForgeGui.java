package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BaseForgeGui implements IForgeGui {
    @Getter
    private final Player viewer;
    private final Set<Player> viewers;

    @Getter
    private final String guiShortName;
    @Getter
    private final ForgeGuiHandler guiHandler;
    @Getter
    private UUID uuid = UUID.randomUUID();

    protected boolean isDisplayed = false;

    @Getter
    private SharedProperty sharedProperty = new SharedProperty();

    public BaseForgeGui(Player viewer, String guiShortName, ForgeGuiHandler guiHandler) {
        this.viewer = viewer;
        this.viewers = new HashSet<>();
        this.viewers.add(viewer);
        this.guiShortName = guiShortName;
        this.guiHandler = guiHandler;
    }

    public void onMessage(JsonObject jsonObject) {

    }

    public void onClose() {

    }

    public void onUpdate() {

    }

    public void markDisplayed() {
        isDisplayed = true;
    }

    public final void display() {
        guiHandler.display(this);
    }

    public final void close() {
        guiHandler.close(this);
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

    @Override
    public Set<Player> getViewers() {
        return viewers;
    }
}

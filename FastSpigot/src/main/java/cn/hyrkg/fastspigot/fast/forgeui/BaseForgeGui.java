package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class BaseForgeGui implements IForgeGui {
    @Getter
    private final Player viewer;
    @Getter
    private final String guiShortName;
    @Getter
    private final ForgeGuiHandler guiHandler;
    @Getter
    private UUID uuid = UUID.randomUUID();

    protected boolean isDisplayed = false;

    @Getter
    private SharedProperty sharedProperty = new SharedProperty();

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
}

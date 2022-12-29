package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;

public class BaseForgeGui extends BaseForgeShareGui {
    @Getter
    private final Player viewer;

    public BaseForgeGui(Player viewer, String guiShortName, ForgeGuiHandler guiHandler) {
        super(guiShortName, guiHandler);
        this.viewer = viewer;
        this.addViewer(viewer);
    }

    @Override
    public void onMessage(Player viewer, JsonObject jsonObject) {
        onMessage(jsonObject);
    }

    @Override
    public void onClose(Player viewer) {
        onClose();
    }

}

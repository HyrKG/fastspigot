package cn.hyrkg.fastspigot.fast.forgeui;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BaseForgeGui extends BaseForgeShareGui {
    @Getter
    private final Player viewer;

    public BaseForgeGui(Player viewer, String guiShortName, ForgeGuiHandler guiHandler) {
        super(guiShortName,guiHandler);
        this.viewer = viewer;
        this.addViewer(viewer);
    }


}

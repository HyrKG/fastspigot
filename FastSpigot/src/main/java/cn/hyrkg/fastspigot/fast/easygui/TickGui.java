package cn.hyrkg.fastspigot.fast.easygui;

import org.bukkit.entity.Player;

public abstract class TickGui extends EasyGui {

    public TickGui(Player p) {
        super(p);
    }

    public abstract void tick();
}

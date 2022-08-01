package cn.hyrkg.fastspigot.spigot.utils;

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;

public class MsgHelper {

    private CommandSender commandSender;
    private String prefix = null;

    public MsgHelper tell(String msg) {
        return this.send("§a- >", msg);
    }

    public MsgHelper hint(String msg) {
        return this.send("§6* >", msg);
    }

    public MsgHelper warm(String msg) {
        return this.send("§c! >", msg);
    }

    public MsgHelper info(String msg) {
        return this.send("§b# >", msg);
    }

    public MsgHelper superWarm(String msg) {
        return this.send("§c§l! >", msg);
    }

    public MsgHelper send(String sign, String msg) {
        this.commandSender.sendMessage(sign + (this.prefix == null ? " " : this.prefix + " ") + msg);
        return this;
    }

    public MsgHelper prefix(String str) {
        this.prefix = str + ">";
        return this;
    }

    public static MsgHelper to(CommandSender sender) {
        return new MsgHelper(sender);
    }

    public MsgHelper(CommandSender commandSender) {
        Preconditions.checkNotNull(commandSender);
        this.commandSender = commandSender;

    }

    public String getPrefix() {
        return this.prefix;
    }
}

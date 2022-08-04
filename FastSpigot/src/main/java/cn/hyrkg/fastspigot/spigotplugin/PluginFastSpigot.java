package cn.hyrkg.fastspigot.spigotplugin;


import cn.hyrkg.fastspigot.fast.actionjam.FastJam;
import cn.hyrkg.fastspigot.fast.easygui.EasyGuiHandler;
import cn.hyrkg.fastspigot.spigotplugin.bstats.Metrics;
import cn.hyrkg.fastspigot.spigot.command.CommandFastSpigot;
import cn.hyrkg.fastspigot.spigotplugin.support.concurrent.BukkitThreadExecutor;
import cn.hyrkg.fastspigot.spigotplugin.support.locker.MysqlLockerManager;
import cn.hyrkg.fastspigot.spigotplugin.support.redis.RedisManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.LoadOrder;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "fastspigot", version = "0.2.4")
@LoadOrder(PluginLoadOrder.STARTUP)
@Commands({@Command(name = "fastspigot")})
public class PluginFastSpigot extends JavaPlugin {
    @Getter
    private Metrics metrics;

    @Getter
    private static PluginFastSpigot instance;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("fastspigot").setExecutor(new CommandFastSpigot());

        //create dirs
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        EasyGuiHandler.init(this);
        FastJam.init(this);
        BukkitThreadExecutor.init(this);
        MysqlLockerManager.init(this);
        RedisManager.init(this);

        //metrics
        metrics = new Metrics(this, 11356);

        //info
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[FASTSPIGOT] SUPPORT ENABLED!");
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[FASTSPIGOT] LEARN MORE ABOUT THIS FRAMEWORK IN GITHUB:");
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "[FASTSPIGOT] (https://)github.com/HyrKG/FastSpigot");
    }

    public void reload() {
        MysqlLockerManager.reload(this);
    }
}

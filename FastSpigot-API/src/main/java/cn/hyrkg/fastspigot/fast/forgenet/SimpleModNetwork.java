package cn.hyrkg.fastspigot.fast.forgenet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;

public class SimpleModNetwork {
    private JavaPlugin plugin;

    private boolean enable = false;
    private String channel;

    public SimpleModNetwork(String channel) {

        this.channel = channel;
    }

    public SimpleModNetwork init(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channel);
        enable = true;
        return this;
    }

    public SimpleModNetwork registerCallback(PluginMessageListener pml) {

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, pml);

        return this;
    }

    public void sendPluginMessage(Player target, String str) {
        try {
            target.sendPluginMessage(plugin, channel, ("@" + str).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public boolean isEnable() {
        return enable;
    }
}

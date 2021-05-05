package cn.hyrkg.originspigot.example;


import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.spigot.FastPlugin;
import cn.hyrkg.fastspigot.spigot.service.command.FastCommand;
import cn.hyrkg.fastspigot.spigot.service.command.IFastCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "ExamplePlugin", version = "1")
public class ExampleFastPlugin extends FastPlugin {

    @Inject
    public YourFastCommandExecutor yourFastCommandExecutor;
}

class YourFastCommandExecutor implements IFastCommandExecutor {

    @FastCommand(index = "a", desc = "your command a")
    public void onA(CommandSender sender) {
        //TODO A
    }

    @FastCommand(index = "b", desc = "your command b")
    public void onB(CommandSender sender, String param) {
        //TODO B
    }

    @Override
    public String[] getCommands() {
        return new String[]{"yourcommand"};
    }
}
package cn.hyrkg.fastspigot.example;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "ExamplePlugin", version = "1")
public class ExampleOriginPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("yourcommand").setExecutor(new YourCommandExecutor());
    }
}

class YourCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("a")) {
                //TODO a
            } else if (args.length == 2 && args[0].equalsIgnoreCase("b")) {
                String param = args[1];
                //TODO b
            }
        }
        return false;
    }
}


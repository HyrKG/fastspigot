package cn.hyrkg.originspigot.example;


import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.spigot.FastPlugin;
import cn.hyrkg.fastspigot.spigot.service.IPluginProvider;
import cn.hyrkg.fastspigot.spigot.service.command.FastCommand;
import cn.hyrkg.fastspigot.spigot.service.command.IFastCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.command.Commands;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "ExamplePlugin", version = "1")
@Commands(@Command(name = "test"))
public class ExampleFastPlugin extends FastPlugin {

    @Inject(name = "你的处理器")
    public YourHandler yourHandler;
}

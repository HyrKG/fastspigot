package cn.hyrkg.originspigot.example;


import cn.hyrkg.fastspigot.innercore.annotation.Inject;
import cn.hyrkg.fastspigot.innercore.annotation.events.OnHandlerInit;
import cn.hyrkg.fastspigot.spigot.FastPlugin;
import cn.hyrkg.fastspigot.spigot.service.ILoggerService;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "ExamplePlugin", version = "1")
public class ExampleFastPlugin extends FastPlugin {

    @Inject
    public YourHandler yourHandler;
}

class YourHandler{

    @OnHandlerInit
    public void onInit() {
    }
}

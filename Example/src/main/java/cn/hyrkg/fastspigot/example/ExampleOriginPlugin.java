package cn.hyrkg.fastspigot.example;


import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

@Plugin(name = "ExamplePlugin", version = "1")
public class ExampleOriginPlugin extends JavaPlugin {

    public YourHandler yourHandler;

    @Override
    public void onEnable() {
        yourHandler = new YourHandler();
        yourHandler.onInit();
    }
}

class YourHandler {

    public void onInit()
    {

    }
}


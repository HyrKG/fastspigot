# FastSpigot
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cd503329e69a48439a0aa1d8cb115b0b)](https://www.codacy.com/gh/HyrKG/FastSpigot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HyrKG/FastSpigot&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/e83a46a7-aa0a-4dbd-ac26-a68ca734dfd4)](https://codebeat.co/projects/github-com-hyrkg-fastspigot-master)
![](https://img.shields.io/badge/language-Java-orange.svg)
![](https://img.shields.io/badge/minecraft-1.12.2-blue.svg)

这是一个帮助进行快速插件开发的库/框架。我将会使用ASM、注释和反射等方法，帮助你进行更加快速与简洁的开发。

This is a lib/framework which can helps you develop spigot plugin faster.
I'll use ASM,annotation or reflection(I don't know how it calls) to help you develop faster and more succinctly.
>该项目主要为学习用途，很多专业知识并不具备，若有错误请指出！
>
> This project is mainly for learning purposes, it means I may make professional mistakes or description mistakes,
> please point it out.

# 一些使用前后对比 Some Before and After
#### 注册事件 Register Event

使用前 Before
```Java
public class ExampleOriginPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new YourEventHandler(), this);
    }
}

class YourEventHandler implements Listener {
    @EventHandler
    public void onEvent(Event event) {
        //TODO
    }
}
```
使用后 After
```Java
public class ExampleFastPlugin extends FastPlugin {
    @Inject
    public YourEventHandler yourEventHandler;
}

class YourEventHandler implements Listener {
    @EventHandler
    public void onEvent(Event event) {
        //TODO
    }
}
```

####  指令 Command
使用前 Before
```Java
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
```
使用后 After
```Java
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
```
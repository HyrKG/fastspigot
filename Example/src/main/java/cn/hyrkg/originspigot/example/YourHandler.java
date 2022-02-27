package cn.hyrkg.originspigot.example;

import cn.hyrkg.fastspigot.spigot.service.command.FastCommand;
import cn.hyrkg.fastspigot.spigot.service.command.IFastCommandExecutor;
import org.bukkit.command.CommandSender;

public class YourHandler implements IFastCommandExecutor {

    @FastCommand(index = "kill", desc = "输入A", paramsName = {"目标", "次数"}, order =0)
    public void onA(CommandSender sender, String target, int times) {
        for (int i = 0; i < times; i++)
            checkPlayer(target).setHealth(0);
    }

    @FastCommand(index = "kill2", desc = "输入A", paramsName = {"目标", "次数"}, order = 1)
    public void onA2(CommandSender sender, String target, int times) {
        for (int i = 0; i < times; i++)
            checkPlayer(target).setHealth(0);
    }

    @Override
    public String[] getCommands() {
        return new String[]{"test"};
    }

}

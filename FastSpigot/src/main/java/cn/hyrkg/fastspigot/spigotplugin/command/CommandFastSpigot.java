package cn.hyrkg.fastspigot.spigotplugin.command;

import cn.hyrkg.fastspigot.spigotplugin.PluginFastSpigot;
import cn.hyrkg.fastspigot.spigotplugin.support.locker.ILock;
import cn.hyrkg.fastspigot.spigotplugin.support.locker.MysqlLocker;
import cn.hyrkg.fastspigot.spigotplugin.support.locker.MysqlLockerManager;
import lombok.SneakyThrows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.FutureTask;

public class CommandFastSpigot implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (s.equalsIgnoreCase("fastspigot")) {
            if (commandSender.isOp()) {
                //op commands
                if (strings.length == 0) {
                    //help page;
                    commandSender.sendMessage("§6>FastSpigot");
                    commandSender.sendMessage("§f- §9/fastspigot reload - reload plugin");
                } else if (strings[0].equalsIgnoreCase("reload")) {
                    PluginFastSpigot.getInstance().reload();
                    commandSender.sendMessage("§6>Reload request sent!");
                } else if (strings[0].equalsIgnoreCase("testlock")) {

                    MysqlLocker mysqlLocker = MysqlLockerManager.getOrCreateLocker("test");
                    ILock lock = mysqlLocker.getLock("test");

                    long timeNow = System.currentTimeMillis();

                    lock.lock();

                    String cost = String.valueOf(System.currentTimeMillis() - timeNow);
                    commandSender.sendMessage("§6>Test lock cost: " + cost + "ms");
                } else if (strings[0].equalsIgnoreCase("testunlock")) {

                    MysqlLocker mysqlLocker = MysqlLockerManager.getOrCreateLocker("test");
                    ILock lock = mysqlLocker.getLock("test");

                    long timeNow = System.currentTimeMillis();
                    lock.unlock();


                    String cost = String.valueOf(System.currentTimeMillis() - timeNow);
                    commandSender.sendMessage("§6>Test unlock cost: " + cost + "ms");
                } else if (strings[0].equalsIgnoreCase("testrun")) {

                    MysqlLocker mysqlLocker = MysqlLockerManager.getOrCreateLocker("test");
                    ILock lock = mysqlLocker.getLock("test");

                    lock.addWaitingExecutor(() -> {
                        System.out.println("run !!");
                    });

                } else if (strings[0].equalsIgnoreCase("testinvoke")) {

                    MysqlLocker mysqlLocker = MysqlLockerManager.getOrCreateLocker("test");
                    ILock lock = mysqlLocker.getLock("test");

                    long timeNow = System.currentTimeMillis();
                    FutureTask futureTask = lock.addLockExecutor(() -> {
                    });
                    while (!futureTask.isDone()) {
                        Thread.sleep(1);
                    }
                    String cost = String.valueOf(System.currentTimeMillis() - timeNow);
                    commandSender.sendMessage("§6>Test lock and run cost: " + cost + "ms");
                }
            } else {
                commandSender.sendMessage("§c> You don't have enough permission!");
            }
        }
        return false;
    }
}

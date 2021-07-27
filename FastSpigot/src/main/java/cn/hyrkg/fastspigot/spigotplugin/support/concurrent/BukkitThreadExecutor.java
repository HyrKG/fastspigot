package cn.hyrkg.fastspigot.spigotplugin.support.concurrent;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class BukkitThreadExecutor {
    @Getter
    private static BukkitThreadExecutor instance;

    private final JavaPlugin plugin;

    private BukkitThreadExecutor(JavaPlugin pluginIn) {
        this.plugin = pluginIn;
    }

    @SneakyThrows
    public <V> V submitAndWait(Callable<V> callable) throws Exception {
        Future<V> futureTask = submit(callable);
        while (!futureTask.isDone()) {
            Thread.sleep(10);
        }
        return futureTask.get();
    }

    public <V> Future<V> submit(Callable<V> callable) throws Exception {
        FutureTask<V> futureTask = new FutureTask<V>(callable);
        Bukkit.getScheduler().runTask(plugin, () -> {
            futureTask.run();
        });
        return futureTask;
    }

    public <V> Future<V> submit(Runnable runnable) throws Exception {
        return submit(Executors.callable(runnable, null));
    }

    //Make sure init in main thread
    public static void init(JavaPlugin javaPlugin) {
        if (instance == null) {
            instance = create(javaPlugin);
        }
    }

    public static BukkitThreadExecutor create(JavaPlugin plugin) {
        BukkitThreadExecutor concurrentManager = new BukkitThreadExecutor(plugin);
        return concurrentManager;
    }


}

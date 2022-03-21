package cn.hyrkg.fastspigot.fast.bungeebridge.network;

import cn.hyrkg.fastspigot.fast.bungeebridge.network.heartbeat.HeartbeatHandler;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BridgePacketHandler {

    private BukkitRunnable bukkitRunnable = null;

    private ConcurrentHashMap<UUID, BungeeRequest> uuidRequestMap = new ConcurrentHashMap<>();

    private int heartbeatCountdown = 0;


    public void init(JavaPlugin plugin) {
        if (bukkitRunnable != null) {
            bukkitRunnable.cancel();
        }

        bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                onAsynTick();
            }
        };
        bukkitRunnable.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    //每秒更新
    private void onAsynTick() {
        uuidRequestMap.values().forEach(j -> {
            if (j.isTimeOut()) {
                j.state = BungeeRequest.SIGNAL_TIME_OUT;
                uuidRequestMap.remove(j);
            }
        });

        heartbeatCountdown += 1;
        if (heartbeatCountdown >= 5) {
            HeartbeatHandler.doHeartBeat();
            heartbeatCountdown = 0;
        }
    }

    public void registerRequest(BungeeRequest request) {
        uuidRequestMap.put(request.uuid, request);
    }

    public void onReceiveResultMessage(JsonObject jsonObject) {
        {
            //packet uuid
            if (!jsonObject.has("uid")) {
                return;
            }

            UUID uuid = UUID.fromString(jsonObject.get("uid").getAsString());

            if (!uuidRequestMap.contains(uuid)) {
                System.out.println("packet outdated! " + uuid + "(" + jsonObject.toString() + ")");
                return;
            }

            //获得结果并且在内存中释放
            //该处无需对外部代码进行通知行为

            BungeeRequest request = uuidRequestMap.get(uuid);
            JsonObject result = jsonObject.has("result") ? jsonObject.getAsJsonObject("result") : new JsonObject();
            request.result = result;
            request.state = BungeeRequest.SIGNAL_DONE;

            uuidRequestMap.remove(uuid);
        }

    }
}

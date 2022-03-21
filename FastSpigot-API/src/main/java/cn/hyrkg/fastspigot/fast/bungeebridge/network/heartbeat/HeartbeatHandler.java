package cn.hyrkg.fastspigot.fast.bungeebridge.network.heartbeat;

import cn.hyrkg.fastspigot.fast.bungeebridge.BungeeBridge;
import com.google.gson.JsonObject;

public class HeartbeatHandler {

    private static long lastUpdateTag = -1;


    public static void doHeartBeat() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("c", "hb");

        if (lastUpdateTag == -1 || lastUpdateTag != BungeeBridge.getLastChanged()) {
            lastUpdateTag = BungeeBridge.getLastChanged();
            jsonObject.add("bridges", BungeeBridge.getBridgeCaches());
        }
    }

}

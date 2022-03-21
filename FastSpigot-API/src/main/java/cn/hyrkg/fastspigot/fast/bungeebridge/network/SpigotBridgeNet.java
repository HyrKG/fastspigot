package cn.hyrkg.fastspigot.fast.bungeebridge.network;

import cn.hyrkg.fastspigot.fast.bungeebridge.BungeeBridge;
import com.google.gson.JsonObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * 该类负责进行Spigot与BungeeCord之间沟通
 */
public class SpigotBridgeNet {

    /**
     * 初始化
     */
    public static final String CHANNEL_NAME = "bgb";
    public static final int PROCESS_RESULT = 1,
            PROCESS_HANDLE = 0;

    private static BridgePacketHandler handler;

    public static void onInit(JavaPlugin plugin) {

    }


    public static void sendRequest(BungeeRequest request) {
        handler.registerRequest(request);
        request.setSentTime(System.currentTimeMillis());
        request.state = BungeeRequest.SIGNAL_SENT;

        //code sent here
    }

    public static void onReceiveMessage(JsonObject jsonObject) {
        int type = jsonObject.get("t").getAsInt();

        //如果为请求处理，分发至请求处理器
        if (type == PROCESS_HANDLE) {
            String bridgeIndex = jsonObject.get("idx").getAsString();
            if (BungeeBridge.hasBridge(bridgeIndex)) {
                UUID uuid = UUID.fromString(jsonObject.get("uid").getAsString());

                BungeeRequest request = new BungeeRequest(uuid);
                request.readJsonObject(jsonObject);
                BungeeBridge.createOrGet(bridgeIndex).onReceiveHandleMessage(null, request);
                //TODO send request back

            }
        }
        //如果为结果，分发至结果处理器
        else if (type == PROCESS_RESULT) {
            handler.onReceiveResultMessage(jsonObject);
        }
    }


    /*
     * 发送消息至Bungee通知注册Bridge
     * */
    public static void registerBungeeBridge(BungeeBridge bungeeBridge) {

    }


}

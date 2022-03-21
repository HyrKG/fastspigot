package cn.hyrkg.fastspigot.fast.bungeebridge;

import cn.hyrkg.fastspigot.fast.bungeebridge.network.BungeeRequest;
import cn.hyrkg.fastspigot.fast.bungeebridge.network.SpigotBridgeNet;
import cn.hyrkg.fastspigot.fast.bungeebridge.network.heartbeat.HeartbeatHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.Array;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class BungeeBridge {
    @Getter
    private static HashMap<String, BungeeBridge> bridgeMap = new HashMap<>();

    @Getter
    private static JsonArray bridgeCaches = new JsonArray();
    @Getter
    private static long lastChanged = -1;

    //该index将会被用于区分不同的订阅
    public final String key;

    private ConcurrentHashMap<Class, IBungeeRequestHandler> requestHandlerMap = new ConcurrentHashMap<>();

    public void onReceiveHandleMessage(ServerInfo info, BungeeRequest request) {
        JsonObject resultMessage = request.getResult();
        for (IBungeeRequestHandler requestHandler : requestHandlerMap.values()) {
            JsonObject result = requestHandler.handlerRequest(info, request.getOriginPacket(), resultMessage);
            if (result == null) {
                break;
            } else {
                resultMessage = result;
            }
        }
        request.setResult(resultMessage);
        //return result
    }


    /**
     * 注册请求处理器
     */
    public void addRequestHandler(IBungeeRequestHandler requestHandler) {
        requestHandlerMap.put(requestHandler.getClass(), requestHandler);
    }

    /*
     * 向BC发送请求
     * ！注意，该方法将会堵塞进程直至收到返回！
     *
     *  @return 返回null表示回执错误或超时
     * */
    @SneakyThrows
    public JsonObject requestAndWait(JsonObject jsonObject) {
        BungeeRequest request = request(jsonObject);
        while (request.isDone()) {
            Thread.sleep(10);
        }
        if (request.isTimeOut()) {
            return null;
        }
        return request.getResult();
    }

    public BungeeRequest request(JsonObject jsonObject) {
        BungeeRequest request = new BungeeRequest(jsonObject);
        SpigotBridgeNet.sendRequest(request);
        return request;
    }


    public static boolean hasBridge(String key) {
        return bridgeMap.containsKey(key);
    }

    /**
     * 创建或获得BungeeBridge，除非自行释放，否则Bridge将会一直存在
     */
    public static BungeeBridge createOrGet(String key) {
        if (!bridgeMap.containsKey(key)) {
            BungeeBridge bungeeBridge = new BungeeBridge(key);
            SpigotBridgeNet.registerBungeeBridge(bungeeBridge);
            bridgeMap.put(key, bungeeBridge);
            updateCacheBridges();
            HeartbeatHandler.doHeartBeat();
        }
        return bridgeMap.get(key);
    }

    public static void updateCacheBridges() {
        lastChanged = System.currentTimeMillis();
        bridgeCaches = new JsonArray();
        bridgeMap.values().forEach(j -> {
            bridgeCaches.add(j.key);
        });
    }

}

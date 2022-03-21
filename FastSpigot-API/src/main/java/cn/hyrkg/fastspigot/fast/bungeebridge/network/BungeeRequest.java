package cn.hyrkg.fastspigot.fast.bungeebridge.network;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class BungeeRequest {

    public static final int
            SIGNAL_CREATED = 0, //Request未发出
            SIGNAL_SENT = 1,//Request已发出
            SIGNAL_DONE = 2,//Request已收到回执
            SIGNAL_TIME_OUT = 3;//Request超时

    @Getter
    protected int state = 0;


    @Getter
    protected final UUID uuid;
    @Getter
    protected JsonObject originPacket;
    @Getter
    @Setter
    protected JsonObject result = new JsonObject();

    @Getter
    @Setter
    private long sentTime = -1;

    public BungeeRequest(JsonObject originPacket) {
        this.originPacket = originPacket;

        uuid = UUID.randomUUID();
    }

    public BungeeRequest(UUID uuid) {
        this.uuid = uuid;
    }


    public boolean isSent() {
        return state >= SIGNAL_SENT;
    }

    public boolean isDone() {
        return state >= SIGNAL_DONE;
    }

    public boolean isTimeOut() {
        return state == SIGNAL_TIME_OUT;
    }

    public void readJsonObject(JsonObject jsonObject) {
        originPacket = jsonObject.getAsJsonObject("origin");
        result = jsonObject.getAsJsonObject("resul");
    }

    public JsonObject saveAsJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("uid", uuid.toString());
        jsonObject.add("origin", originPacket);
        jsonObject.add("result", result);
        return jsonObject;
    }

}

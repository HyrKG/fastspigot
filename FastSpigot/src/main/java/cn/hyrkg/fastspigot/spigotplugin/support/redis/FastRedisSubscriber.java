package cn.hyrkg.fastspigot.spigotplugin.support.redis;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.UUID;

public abstract class FastRedisSubscriber extends JedisPubSub {

    public static final String PROPERTY_KEY_UID = "$uid";
    public static final JsonParser parser = new JsonParser();

    protected UUID uuid = UUID.randomUUID();

    //是否自我忽略，不接收自己发出的包
    protected boolean selfNeglect = false;
    //重连时间
    protected long reconnectTime = 5000;

    @Getter
    protected HashMap<String, FastRedisChannel> bindingChannel = new HashMap<>();

    public void publish(FastRedisChannel channel, JsonObject jsonObject) {
        if (selfNeglect) jsonObject.addProperty(PROPERTY_KEY_UID, uuid.toString());
        channel.publish(jsonObject);
    }

    public void publishAll(JsonObject jsonObject) {
        bindingChannel.values().forEach(j -> this.publish(j, jsonObject));
    }

    public void onPreSubscribe() {
    }


    public void onPostSubscribe() {
    }

    public abstract void onMessage(FastRedisChannel channel, JsonObject jsonObject);

    @Override
    public void onMessage(String channel, String message) {
        FastRedisChannel fastRedisChannel = bindingChannel.get(channel);
        if (fastRedisChannel != null) {
            try {
                JsonObject jsonObject = parser.parse(message).getAsJsonObject();
                if (selfNeglect && jsonObject.has(PROPERTY_KEY_UID) && jsonObject.get(PROPERTY_KEY_UID).getAsString().equals(uuid.toString())) {
                    return;
                }
                onMessage(fastRedisChannel, jsonObject);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("<redis-client> cannot handle message " + message + " from " + channel);
            }
        }
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        super.onUnsubscribe(channel, subscribedChannels);
    }

    @SneakyThrows
    public synchronized void onUnsubscribeUnexpected(FastRedisChannel channel, Exception exception) {
        Thread.sleep(reconnectTime);
        //reconnect
        channel.subscribeAndBlocking(this);
    }
}

package cn.hyrkg.fastspigot.spigotplugin.support.redis;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FastRedisChannel {
    public final String channelName;

    public void subscribeAsync(FastRedisSubscriber fastRedisPubSub) {
        fastRedisPubSub.getBindingChannel().put(channelName, this);
        RedisManager.getThreadPool().submit(() -> subscribeAndBlocking(fastRedisPubSub));
    }

    public void subscribeAndBlocking(FastRedisSubscriber fastRedisPubSub) {
        try {
            fastRedisPubSub.onPreSubscribe();
            RedisManager.getJedis().subscribe(fastRedisPubSub, channelName);
            fastRedisPubSub.onPostSubscribe();
        } catch (Exception e) {
            fastRedisPubSub.onUnsubscribeUnexpected(this, e);
        }
    }

    public void publish(JsonObject jsonObject) {
        jsonObject.addProperty("$server_port", RedisManager.getServerPort());
        RedisManager.getJedis().publish(channelName, jsonObject.toString());
    }
}

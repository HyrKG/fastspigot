package cn.hyrkg.fastspigot.spigotplugin.support.redis;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;

@RequiredArgsConstructor
public class FastRedisChannel {
    public final String channelName;

    public void subscribeAsync(FastRedisSubscriber fastRedisPubSub) {
        fastRedisPubSub.getBindingChannel().put(channelName, this);
        RedisManager.getThreadPool().submit(() -> subscribeAndBlocking(fastRedisPubSub));
    }

    public void subscribeAndBlocking(FastRedisSubscriber fastRedisPubSub) {

        try (Jedis jedis = RedisManager.getNewJedis()) {
            fastRedisPubSub.onPreSubscribe();
            jedis.subscribe(fastRedisPubSub, channelName);
        } catch (Exception e) {
            fastRedisPubSub.onUnsubscribeUnexpected(this, e);
        }
    }

    public void publish(JsonObject jsonObject) {
        jsonObject.addProperty("$server_port", RedisManager.getServerPort());
        try (Jedis jedis = RedisManager.getJedis()) {
            jedis.publish(channelName, jsonObject.toString());
        }
    }
}

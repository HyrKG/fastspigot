package cn.hyrkg.lockertest;

import cn.hyrkg.fastspigot.spigotplugin.support.redis.FastRedisChannel;
import cn.hyrkg.fastspigot.spigotplugin.support.redis.FastRedisSubscriber;
import cn.hyrkg.fastspigot.spigotplugin.support.redis.RedisManager;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;

public class LockerTest {
    @SneakyThrows
    public static void main(String[] args) {
        RedisManager.setHost("hyrkg.cn");
        RedisManager.setPassword("passwd123456");

        FastRedisChannel channel = new FastRedisChannel("test");

        FastRedisSubscriber subscriber1 = new FastRedisSubscriber() {
            @Override
            public void onMessage(FastRedisChannel channel, JsonObject jsonObject) {
                System.out.println("msg1 to " + this + "> " + jsonObject);
            }
        };

        FastRedisSubscriber subscriber2 = new FastRedisSubscriber() {
            @Override
            public void onMessage(FastRedisChannel channel, JsonObject jsonObject) {
                System.out.println("msg2 to " + this + "> " + jsonObject);
            }
        };

        channel.subscribeAsync(subscriber1);
        channel.subscribeAsync(subscriber2);

        Thread.sleep(2000);
        subscriber1.publishAll(new JsonObject());
        subscriber2.publishAll(new JsonObject());


    }
}

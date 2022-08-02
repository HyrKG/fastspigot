package cn.hyrkg.lockertest;

import cn.hyrkg.fastspigot.spigotplugin.support.redis.RedisManager;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class LockerTest {
    @SneakyThrows
    public static void main(String[] args) {
        RedisManager.setHost("hyrkg.cn");
        RedisManager.setPassword("passwd123456");

        try (Jedis jedis = RedisManager.getJedis()) {
            long timeBefore = System.currentTimeMillis();
            for (int i = 0; i < 8462; i++) {
                if (i % 50 == 0) System.out.println("insert progress " + i);
                jedis.lpush("saochatlist:dating", i + ">>" + UUID.randomUUID().toString());
            }
            System.out.println("insert cost " + (System.currentTimeMillis() - timeBefore));
        }

        try (Jedis jedis = RedisManager.getJedis()) {
            long timeBefore = System.currentTimeMillis();
            jedis.lrange("saochatlist:dating", 0, -1);
            System.out.println("get cost " + (System.currentTimeMillis() - timeBefore));
        }


    }
}

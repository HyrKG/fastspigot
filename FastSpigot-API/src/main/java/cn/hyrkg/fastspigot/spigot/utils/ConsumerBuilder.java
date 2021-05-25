package cn.hyrkg.fastspigot.spigot.utils;

import java.util.function.Consumer;

public class ConsumerBuilder<T> {

    private T object;

    private ConsumerBuilder(T t) {
        this.object = t;
    }

    public void accept(Consumer<T> consumer) {
        consumer.accept(object);
    }

    public static <T> ConsumerBuilder<T> of(T object) {
        return new ConsumerBuilder<T>(object);
    }
}

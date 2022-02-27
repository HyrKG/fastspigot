package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import java.util.concurrent.FutureTask;

/**
 * 锁的实例，具体到玩家。提供了锁的解锁和锁的加锁。同时提供了加锁执行与等待执行的功能。
 * */
public interface ILock {
    /**
     * 是否上锁，该方法将会考虑到过期时间。
     * */
    boolean isLocked();

    /**
     * 上锁，通常不推荐您直接调用，而是通过下方addLockExecutor进行隐性调用。
     * */
    void lock();

    /**
     * 解锁，通常不推荐您直接使用，理由同上。
     * */
    void unlock();

    /*
    * 该数据是否存在数据库
    * */
    boolean hasData();

    /**
     * 增加等待解锁的的任务.
     * 您的任务将会被加入队列，直到解锁后被依次执行。
     *
     * @return 将会返回一个封装了您的runnable的FutureTask，您可以随时取消或者查询状态。
     *
     * */
    FutureTask addWaitingExecutor(Runnable runnable);

    /**
     * 加锁执行你的任务，逻辑如下：上锁-》执行您的runnable-》解锁。
     *
     * 通常情况下，我们推荐使用该方法进行加锁操作，而不是直接调用lock和unlock。
     * ！！注意！！使用该方法，你的runnable将会被异步执行，如有必要预先准备相关数据。
     *
     * @return 将会返回一个封装了您的runnable的FutureTask，您无法取消，但可以随时查询状态。
     * */
    FutureTask addLockExecutor(Runnable runnable);
}

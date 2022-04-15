package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.kg.fast.inject.mysql3_1.ReleasableConnection;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;

@RequiredArgsConstructor
@Getter
public class AsynLock implements ILock {

    private static PreparedStatement cacheStatement;

    public static final String MYSQL_SELECT = "SELECT %s FROM %s WHERE %s='%s' LIMIT 1;";

    protected final MysqlLocker locker;
    protected final String key;

    protected Queue<FutureTask> runnableQueue = new ConcurrentLinkedQueue<>();
    protected FutureTask<Void> futureTask = null;
    protected long expirationTime = 15;

    @Override
    public boolean isLocked() {
        boolean lockDataState = getLock();
        if (lockDataState) {
            long lastUpdate = getLastUpdate();

            //if is expiration, then unlock
            if (lastUpdate > 0) {
                if (System.currentTimeMillis() - lastUpdate > expirationTime * 1000) {
                    lockDataState = false;
                }
            }
        }
        return lockDataState;
    }

    public synchronized FutureTask addLockExecutor(Runnable runnable) {
        FutureTask<Void> task = new FutureTask<Void>(() -> {
            try {
                lock();
                runnable.run();
            } finally {
                unlock();
            }
            return null;
        });
        MysqlLockerManager.getExecutorService().execute(task);
        return task;
    }

    @Override
    public ILock setExpirationTime(long time) {
        this.expirationTime = time;
        return this;
    }

    public synchronized FutureTask addWaitingExecutor(Runnable runnable) {
        FutureTask futureTaskInput = new FutureTask(() -> {
            runnable.run();
            return null;
        });

        runnableQueue.add(futureTaskInput);
        if (futureTask == null || futureTask.isDone() || futureTask.isCancelled()) {
            MysqlLockerManager.getExecutorService().execute(futureTask = new FutureTask<>(this::startQueue));
        }
        return futureTask;
    }

    @SneakyThrows
    public Void startQueue() {
        while (!runnableQueue.isEmpty()) {
            while (!isLocked()) {
                FutureTask task = runnableQueue.poll();
                if (!task.isCancelled()) {
                    task.run();
                }
                //Sleep to wait for lock if is needed
                Thread.sleep(5);
            }
            //TAKE A NAP~
            Thread.sleep(50);
        }
        return null;
    }

    @Override
    @SneakyThrows
    public void lock() {
//        String update = "UPDATE %s SET %s=?,%s=? WHERE %s=? LIMIT 1;";
//        String mysql = String.format(update, locker.getTableName(), MysqlLocker.FLAG_LOCK, MysqlLocker.FLAG_LAST, MysqlLocker.FLAG_UUID);
//        UnsafeQuery query = locker.mysql(mysql);
//        query.set(1, 1);
//        query.set(2, System.currentTimeMillis());
//        query.set(3, key);
        String model = "UPDATE " + locker.getTableName() + " SET %s WHERE " + MysqlLocker.FLAG_UUID + "='" + key + "' LIMIT 1;";
        String updateLok = String.format(model, MysqlLocker.FLAG_LOCK + "=1," + MysqlLocker.FLAG_LAST + "=0");

        ReleasableConnection connection = locker.getPool().getConnection();
        Statement statement = connection.getConnection().createStatement();

        //pre-lock and update after lock to make sure the lock is successful
        int changed = statement.executeUpdate(updateLok);

        //update time if the lock is exists
        if (changed != 0) {
            String updateTime = String.format(model, MysqlLocker.FLAG_LAST + "=" + System.currentTimeMillis());
            statement.executeUpdate(updateTime);
            connection.release();
        } else {
            connection.release();
            insertData(true);
        }
    }

    @Override
    @SneakyThrows
    public void unlock() {
        String update = "UPDATE " + locker.getTableName() + " SET " + MysqlLocker.FLAG_LOCK + "=0 WHERE " + MysqlLocker.FLAG_UUID + "='" + key + "' LIMIT 1;";
        ReleasableConnection connection = locker.getPool().getConnection();
        int changed = connection.getConnection().createStatement().executeUpdate(update);
        connection.release();
        if (changed == 0) {
            insertData(false);
        }
    }

    @Override
    @SneakyThrows
    public boolean hasData() {
        String mysql = String.format(MYSQL_SELECT, "0", locker.getTableName(), MysqlLocker.FLAG_UUID, key);
        return locker.mysql(mysql).executeQuery().next();
    }

    @SneakyThrows
    public void insertData(boolean defaultLock) {
        if (defaultLock) {
            String mysql = "INSERT INTO " + locker.getTableName() + "(" + MysqlLocker.FLAG_UUID + "," + MysqlLocker.FLAG_LOCK + ") VALUES('" + key + "',1);";
            ReleasableConnection connection = locker.getPool().getConnection();

            Statement statement = connection.getConnection().createStatement();
            statement.execute(mysql);
            String model = "UPDATE " + locker.getTableName() + " SET " + MysqlLocker.FLAG_LAST + "=" + System.currentTimeMillis() + " WHERE " + MysqlLocker.FLAG_UUID + "='" + key + "' LIMIT 1;";
            statement.execute(model);

            connection.getConnection().createStatement().executeUpdate(model);
            connection.release();
        } else {
            String mysql = "INSERT INTO " + locker.getTableName() + "(" + MysqlLocker.FLAG_UUID + ") VALUES('" + key + "');";
            String insert = String.format(mysql, locker.getTableName(), MysqlLocker.FLAG_UUID);
            ReleasableConnection connection = locker.getPool().getConnection();
            connection.getConnection().createStatement().executeUpdate(insert);
            connection.release();
        }

    }

    @SneakyThrows
    protected ResultSet selectResult(String flag) {
        String mysql = String.format(MYSQL_SELECT, flag, locker.getTableName(), MysqlLocker.FLAG_UUID, key);
        return locker.mysql(mysql).executeQuery();
    }

    @SneakyThrows
    protected boolean getLock() {
        ResultSet result = selectResult(MysqlLocker.FLAG_LOCK);
        if (result.next())
            return result.getBoolean(1);
        return false;
    }

    @SneakyThrows
    protected long getLastUpdate() {
        ResultSet result = selectResult(MysqlLocker.FLAG_LAST);
        if (result.next())
            return result.getLong(1);
        return 0;
    }

    @SneakyThrows
    protected JsonObject getExtData() {
        ResultSet resultSet = selectResult(MysqlLocker.FLAG_DATA);
        if (resultSet.next()) {
            byte[] bytes = resultSet.getBytes(1);
            return new JsonParser().parse(new String(bytes, StandardCharsets.UTF_8)).getAsJsonObject();
        }
        return new JsonObject();
    }

}

package cn.hyrkg.fastspigot.spigotplugin.support.locker;

import cn.hyrkg.fastspigot.spigot.service.simplemysql.ISimpleMysql;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.kg.fast.inject.mysql3_1.SimpleMysqlPool;

@RequiredArgsConstructor
@Getter
public class MysqlLocker implements ISimpleMysql {
    private final String tableName;

    public static final String FLAG_UUID = "uuid", FLAG_LOCK = "lok", FLAG_LAST = "last", FLAG_DATA = "ext";

    @SneakyThrows
    public void initTable() {
        if (!MysqlLockerManager.isEnabled()) {
            return;
        }
        //create mysql table if not exists,
        //key tinytext unique,lock tinyint default 0,last bigint default -1,data blob default null
        String createMysql = String.format("CREATE TABLE IF NOT EXISTS %s(%s)", tableName,
                FLAG_UUID + " TINYTEXT NOT NULL," +
                        FLAG_LOCK + " TINYINT DEFAULT 0," +
                        FLAG_LAST + " BIGINT DEFAULT 0," +
                        FLAG_DATA + " BLOB DEFAULT NULL,"
                        + "UNIQUE KEY uuidkey(" + FLAG_UUID + "(32))"
        );
        mysql(createMysql).executeUpdate();
    }

    public ILock getLock(String key) {
        if (MysqlLockerManager.isEnabled())
            return new AsynLock(this, key);
        return EmptyLock.EMPTY_LOCK;
    }

    @Override
    public SimpleMysqlPool getPool() {
        return MysqlLockerManager.getSimpleMysqlPool();
    }
}

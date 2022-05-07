package me.kg.fast.inject.mysql3_1;

import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReleasableConnection {

    private SimpleMysqlPool thePool;
    @Getter
    private Connection connection;

    public void release() {
        thePool.returnConnection(this);
    }

    private ReleasableConnection(SimpleMysqlPool pool, Connection connection) {
        this.thePool = pool;
        this.connection = connection;
    }

    public static ReleasableConnection link(SimpleMysqlPool pool, Connection connection) {
        return new ReleasableConnection(pool, connection);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement prepareStatement(String mysql) {
        try {
            return connection.prepareStatement(mysql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

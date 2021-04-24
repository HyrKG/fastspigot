package me.kg.fast.inject.mysql2_2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UnsafeQuery {
    private static me.kg.fast.inject.mysql2_2.SimpleMysqlPool globalPool = null;

    private final String mysql;
    protected me.kg.fast.inject.mysql2_2.SimpleMysqlPool selfPool = null;
    private PreparedStatement preparedStatement;
    private me.kg.fast.inject.mysql2_2.ReleasableConnection connection;


    public UnsafeQuery(String mysql) throws Exception {
        this.mysql = mysql;
        prepareMysql();
    }

    public UnsafeQuery(String mysql, me.kg.fast.inject.mysql2_2.SimpleMysqlPool pool) throws Exception {
        this.mysql = mysql;
        this.selfPool = pool;
        prepareMysql();
    }

    public me.kg.fast.inject.mysql2_2.SimpleMysqlPool getUsingPool() {
        return selfPool == null ? globalPool : selfPool;
    }


    protected void prepareMysql() throws Exception {
        if (getUsingPool() == null)
            throw new Exception("Empty Pool Setting");
        preparedStatement = (connection = getUsingPool().getConnection()).prepareStatement(mysql);
    }

    public void set(int i, Object obj) throws SQLException {
        preparedStatement.setObject(i, obj);
    }

    public void addBatch() throws SQLException {
        preparedStatement.addBatch();
    }

    public int[] executeBath() throws SQLException {
        try {
            int[] result = preparedStatement.executeBatch();
            return result;
        } finally {
            connection.release();
        }
    }

    public long[] executeLargeBath() throws SQLException {
        try {
            long[] result = preparedStatement.executeLargeBatch();
            return result;
        } finally {
            connection.release();
        }
    }

    public boolean execute() throws SQLException {
        try {
            return preparedStatement.execute();
        } finally {
            connection.release();
        }
    }

    public ResultSet executeQuery() throws SQLException {
        ResultSet set = preparedStatement.executeQuery();
        try {
            return set;
        } finally {
            connection.release();
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            int result = preparedStatement.executeUpdate();
            return result;
        } finally {
            connection.release();
        }
    }

}

package me.kg.fast.inject.mysql3_1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SimpleMysqlPool {

    private Timer connectionKeeper = null;
    private int poolSize = 0;
    private List<ReleasableConnection> createdConnection = new ArrayList<ReleasableConnection>();
    private List<ReleasableConnection> connectionPool = new ArrayList<ReleasableConnection>();

    public static SimpleMysqlPool init(int size) {
        SimpleMysqlPool pool = new SimpleMysqlPool(size);


        pool.connectionKeeper = new Timer();
        pool.connectionKeeper.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pool.refresh();
            }
        }, 600000, 600000);
        return pool;
    }

    public static SimpleMysqlPool init(int size, String url, String user, String password) throws SQLException {
        SimpleMysqlPool pool = new SimpleMysqlPool(size);
        pool.connect(url, user, password);
        return pool;
    }


    private SimpleMysqlPool(int size) {
        this.poolSize = size;
    }

    public void refresh() {
        try {
            ReleasableConnection rc = getConnection();
            rc.prepareStatement("SELECT 0 LIMIT 1;").executeQuery();
            rc.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SimpleMysqlPool connect(String url, String user, String password) throws SQLException {
        closePool();
        String tempUrl = url;
            if (!tempUrl.contains("jdbc:mysql://")) {
                tempUrl = "jdbc:mysql://" + tempUrl;
            }
            for (int i = 0; i < poolSize; i++) {
                Connection connection = DriverManager.getConnection(tempUrl, user, password);
                createdConnection.add(ReleasableConnection.link(this, connection));
            }
            connectionPool.addAll(createdConnection);
        return this;
    }

    public synchronized ReleasableConnection getConnection() {
        try {
            while (connectionPool.isEmpty())
                this.wait();
            ReleasableConnection connection = connectionPool.remove(0);
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void closePool() {
        try {
            for (ReleasableConnection rc : createdConnection)
                rc.close();
            createdConnection.clear();
            connectionPool.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UnsafeQuery createQuery(String mysql) throws Exception {
        return new UnsafeQuery(mysql, this);
    }

    protected synchronized void returnConnection(ReleasableConnection connection) {
        this.connectionPool.add(connection);
        this.notifyAll();
    }

}

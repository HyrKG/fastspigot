package me.kg.fast.inject.mysql3_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReleasableConnection {

	private SimpleMysqlPool thePool;
	private Connection theConnection;

	public void release() {
		thePool.returnConnection(this);
	}

	private ReleasableConnection(SimpleMysqlPool pool, Connection connection) {
		this.thePool = pool;
		this.theConnection = connection;
	}

	public static ReleasableConnection link(SimpleMysqlPool pool, Connection connection) {
		return new ReleasableConnection(pool, connection);
	}

	public void close() {
		try {
			theConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public PreparedStatement prepareStatement(String mysql) {
		try {
			return theConnection.prepareStatement(mysql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}

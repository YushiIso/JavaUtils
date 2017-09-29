package com.github.yi.database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseDatabaseClient implements Closeable {
	private final ConnectionSource connectionSource;
	protected final TransactionManager transactionManager;

	public BaseDatabaseClient(final String url, final String user, final String pass) throws SQLException {
		this.connectionSource = makeConnectionSource(url, user, pass);
		this.transactionManager = new TransactionManager(this.connectionSource);
	}

	protected static ConnectionSource makeConnectionSource(final String url, final String user, final String pass)
			throws SQLException {
		return new JdbcPooledConnectionSource(url, user, pass);
	}

	public static <T, O> Optional<T> selectUniqColumn(final Dao<T, Integer> dao, final String columnName, final O o)
			throws SQLException {
		QueryBuilder<T, Integer> qb = dao.queryBuilder();
		return Optional.ofNullable(qb.limit(1l).where().eq(columnName, o).queryForFirst());
	}

	public <T> Dao<T, Integer> createDao(final Class<T> cls) throws SQLException {
		return DaoManager.createDao(this.connectionSource, cls);
	}

	public static <T> QueryBuilder<T, Integer> createQueryBuilder(final Dao<T, Integer> dao) {
		return dao.queryBuilder();
	}

	@Override
	public void close() {
		try {
			this.connectionSource.close();
		} catch (IOException e) {
			log.error("connectionのcloseに失敗", e);
		}

	}

}

package com.shizy.bookreader.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.shizy.bookreader.bean.Book;

import java.sql.SQLException;

public class BookDao extends BaseDaoImpl<Book, Integer> {

	public BookDao(Class<Book> dataClass) throws SQLException {
		super(dataClass);
	}

	public BookDao(ConnectionSource connectionSource, Class<Book> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}

	public BookDao(ConnectionSource connectionSource, DatabaseTableConfig<Book> tableConfig) throws SQLException {
		super(connectionSource, tableConfig);
	}

}

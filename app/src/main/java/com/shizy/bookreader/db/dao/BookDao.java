package com.shizy.bookreader.db.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTableConfig;
import com.shizy.bookreader.bean.Book;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * 查询最后阅读位置
	 *
	 * @param book
	 * @return
	 */
	public int queryReadChapter(Book book) {
		if (book != null) {
			try {
				Map<String, Object> fieldValues = new HashMap<>();
				fieldValues.put(Book.COLUMN_NAME, book.getName());
				fieldValues.put(Book.COLUMN_AUTHOR, book.getAuthor());

				List<Book> result = queryForFieldValues(fieldValues);
				if (result != null && result.size() > 0) {
					return result.get(0).getReadChapter();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 更新最后阅读位置
	 *
	 * @param book
	 * @param readChapter
	 * @return
	 */
	public boolean updateReadChapter(Book book, int readChapter) {
		return updateColumnValue(book, new String[]{Book.COLUMN_READ_CHAPTER}, new Integer[]{readChapter});
	}

	public boolean updateBookSite(Book book) {
		final String[] names = {Book.COLUMN_URL, Book.COLUMN_SITE_NAME};
		final Object[] values = {book.getUrl(), book.getSiteName()};
		return updateColumnValue(book, names, values);
	}

	/**
	 * 更新最新章节
	 *
	 * @param book
	 * @param latestChapter
	 * @return
	 */
	public boolean updateLatestChapter(Book book, String latestChapter) {
		return updateColumnValue(book, new String[]{Book.COLUMN_LATEST_CHAPTER}, new String[]{latestChapter});
	}

	private boolean updateColumnValue(Book book, String[] columnNames, Object[] values) {
		if (book == null) {
			return false;
		}
		if (columnNames == null || values == null) {
			return false;
		}
		if (columnNames.length != values.length) {
			return false;
		}
		try {
			UpdateBuilder<Book, Integer> ub = updateBuilder();
			Where<Book, Integer> where = ub.where();

			where.eq(Book.COLUMN_NAME, book.getName());
			where.and();
			where.eq(Book.COLUMN_AUTHOR, book.getAuthor());

			for (int i = 0; i < columnNames.length; i++) {
				ub.updateColumnValue(columnNames[i], values[i]);
			}

			return ub.update() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}

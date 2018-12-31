package com.shizy.bookreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.db.dao.BookDao;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();

	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 2;

	private BookDao mBookDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Book.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Location.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public BookDao getBookDao() throws SQLException {
		if (mBookDao == null) {
			mBookDao = getDao(Book.class);
		}
		return mBookDao;
	}

	@Override
	public void close() {
		super.close();
		mBookDao = null;
	}

	public static DatabaseHelper getHelper(Context context) {
		return OpenHelperManager.getHelper(context, DatabaseHelper.class);
	}

	public static void releaseHelper() {
		OpenHelperManager.releaseHelper();
	}

}

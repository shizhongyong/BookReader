package com.shizy.bookreader.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.shizy.bookreader.db.dao.BookDao;

@DatabaseTable(tableName = "book", daoClass = BookDao.class)
public class Book extends BaseBean {

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_URL = "url";
	public static final String COLUMN_LATEST_CHAPTER = "latest_chapter";
	public static final String COLUMN_READ_CHAPTER = "read_chapter";
	public static final String COLUMN_SITE_NAME = "site_name";

	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField(uniqueCombo = true, columnName = COLUMN_NAME)
	private String name;
	@DatabaseField(uniqueCombo = true, columnName = COLUMN_AUTHOR)
	private String author;
	@DatabaseField(columnName = COLUMN_URL)
	private String url;
	@DatabaseField
	private String updateTime;
	@DatabaseField(columnName = COLUMN_LATEST_CHAPTER)
	private String latestChapter;
	@DatabaseField
	private String size;
	@DatabaseField
	private String poster;
	@DatabaseField(columnName = COLUMN_SITE_NAME)
	private String siteName;
	@DatabaseField(defaultValue = "0", columnName = COLUMN_READ_CHAPTER)
	private int readChapter;// 保存读到的位置

	public Book() {
	}

	public Book(String name, String author, String url, String updateTime, String latestChapter, String size, String poster, String siteName) {
		this.name = name;
		this.author = author;
		this.url = url;
		this.updateTime = updateTime;
		this.latestChapter = latestChapter;
		this.size = size;
		this.poster = poster;
		this.siteName = siteName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getLatestChapter() {
		return latestChapter;
	}

	public void setLatestChapter(String latestChapter) {
		this.latestChapter = latestChapter;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public int getReadChapter() {
		return readChapter;
	}

	public void setReadChapter(int readChapter) {
		this.readChapter = readChapter;
	}
}

package com.shizy.bookreader.bean;

public class Book extends BaseBean {

	private String name;
	private String author;
	private String url;
	private String updateTime;
	private String latestChapter;
	private String size;
	private String poster;

	private String siteName;

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
}

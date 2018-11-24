package com.shizy.bookreader.bean;

import java.util.List;

public class Catalog extends BaseBean {

	private List<Chapter> chapters;

	public List<Chapter> getChapters() {
		return chapters;
	}

	public void setChapters(List<Chapter> chapters) {
		this.chapters = chapters;
	}
}

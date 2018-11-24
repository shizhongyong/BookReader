package com.shizy.bookreader.site;

import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Chapter;

import java.util.List;

public abstract class Site {

	public abstract List<Book> search(String keyword) throws Exception;

	public abstract List<Chapter> listChapters(String url) throws Exception;

	public abstract List<String> listContent(String chapterUrl) throws Exception;

	public abstract String getName();

	public String charset() {
		return "gbk";
	}

	@Override
	public String toString() {
		return getName();
	}
}

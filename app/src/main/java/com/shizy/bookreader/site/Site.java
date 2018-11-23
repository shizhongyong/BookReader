package com.shizy.bookreader.site;

import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Catalog;

import java.util.List;

public abstract class Site {

	public abstract List<Book> search(String bookName) throws Exception;

	public abstract List<Catalog> parseCatalog(String catalogHtml, String url);

	public abstract List<String> parseContent(String chapterHtml);

	public abstract String getSiteName();

	public String getEncodeType() {
		return "gbk";
	}

	@Override
	public String toString() {
		return getSiteName();
	}
}

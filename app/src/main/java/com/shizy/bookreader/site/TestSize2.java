package com.shizy.bookreader.site;

import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Catalog;

import java.util.List;

public class TestSize2 extends Site {

	@Override
	public List<Book> search(String bookName) throws Exception {
		return null;
	}

	@Override
	public List<Catalog> parseCatalog(String catalogHtml, String url) {
		return null;
	}

	@Override
	public List<String> parseContent(String chapterHtml) {
		return null;
	}

	@Override
	public String getSiteName() {
		return "测试2";
	}
}

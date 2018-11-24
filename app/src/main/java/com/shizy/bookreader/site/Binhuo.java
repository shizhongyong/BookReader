package com.shizy.bookreader.site;

import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Chapter;
import com.shizy.bookreader.util.ParseUtil;
import com.shizy.bookreader.util.NetUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 冰火中文
 */
public class Binhuo extends Site {

	private static final String SITE_NAME = "冰火中文";
	private static final String SITE_HOME = "https://www.binhuo.com";

	@Override
	public List<Book> search(String keyword) throws Exception {
		final String url = "https://www.binhuo.com/modules/article/search.php";
		final String key = "searchkey";

		Connection conn = Jsoup.connect(url)
				.userAgent(NetUtil.USER_AGENT)
				.data(key, keyword);

		Connection.Request request = conn.request();
		request.postDataCharset(charset());

		Document doc = conn.post();

		Element table = doc.select("div.bd > table").first();
		Elements trs = table.getElementsByTag("tbody").first().getElementsByTag("tr");

		final List<Book> books = new ArrayList<>();
		for (Element tr : trs) {
			Element nameElement = tr.select("a.name").first();
			String name = nameElement.text();
			String home = nameElement.attr("abs:href");
			String author = tr.select("a.author").first().text();
			String updateTime = tr.select(".time").first().text();
			String latestChapter = tr.select("a.chapter").first().text();
			String size = tr.getElementsByTag("td").get(4).text();

			books.add(new Book(name, author, home, updateTime, latestChapter, size, null, getName()));
		}
		return books;
	}

	@Override
	public List<Chapter> listChapters(String url) throws Exception {
		Document doc = Jsoup.connect(url)
				.userAgent(NetUtil.USER_AGENT)
				.get();

		Elements sections = doc.getElementsByClass("float-list fill-block");
		final List<Chapter> chapters = new ArrayList<>();
		for (Element section : sections) {
			Elements all = section.select("li > a");
			for (Element a : all) {
				chapters.add(new Chapter(a.text(), a.attr("abs:href")));
			}
		}
		return chapters;
	}

	@Override
	public List<String> listContent(String chapterUrl) throws Exception {
		Document doc = Jsoup.connect(chapterUrl)
				.userAgent(NetUtil.USER_AGENT)
				.get();

		Element content = doc.getElementById("ChapterContents");
		return ParseUtil.parseContentByNodes(content.textNodes());
	}

	@Override
	public String getName() {
		return SITE_NAME;
	}
}

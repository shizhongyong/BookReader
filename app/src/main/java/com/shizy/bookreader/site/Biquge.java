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
import java.util.Collections;
import java.util.List;

public class Biquge extends Site {

	private static final String SITE_NAME = "笔趣阁";
	private static final String SITE_HOME = "http://www.biquge.com.tw";

	@Override
	public List<Book> search(String keyword) throws Exception {
		String url = "http://www.biquge.com.tw/modules/article/soshu.php";
		String key = "searchkey";

		Connection conn = Jsoup.connect(url)
				.userAgent(NetUtil.USER_AGENT)
				.data(key, keyword);
		Connection.Request request = conn.request();
		request.defaultCharset(charset());

		Document doc = conn.get();
		String title = doc.title();
		if (title.contains(keyword)) {
			return Collections.singletonList(bookFromHome(doc));
		} else {
			return booksFromSearch(doc);
		}
	}

	private Book bookFromHome(Document doc) {
		Element info = doc.select("div#info").first();
		String name = doc.getElementsByTag("h1").first().text();
		String home = doc.baseUri();
		String poster = doc.select("div#fmimg > img").first().attr("abs:src");

		String latestChapter = null;
		String author = null;
		String updateTime = null;
		Elements ps = info.getElementsByTag("p");
		for (Element p : ps) {
			String text = p.text();
			int beginIndex = text.indexOf("：") + 1;
			if (text.contains("者：")) {
				author = text.substring(beginIndex);
			}
			if (text.contains("最后更新：")) {
				updateTime = text.substring(beginIndex);
			}
			if (text.contains("最新章节：")) {
				latestChapter = text.substring(beginIndex);
			}
		}
		return new Book(name, author, home, updateTime, latestChapter, null, poster, getName());
	}

	private List<Book> booksFromSearch(Document doc) {
		Element table = doc.select("div#content > table").first();
		Elements trs = table.select("tr#nr");

		final List<Book> books = new ArrayList<>();
		for (Element tr : trs) {
			Elements tds = tr.getElementsByTag("td");
			if (tds == null || tds.size() == 0) {
				continue;
			}
			Element nameElement = tds.get(0).getElementsByTag("a").first();
			String name = nameElement.text();
			String home = nameElement.attr("abs:href");
			String latestChapter = tds.get(1).getElementsByTag("a").first().text();
			String author = tds.get(2).text();
			String size = tds.get(3).text();
			String updateTime = tds.get(3).text();

			books.add(new Book(name, author, home, updateTime, latestChapter, size, null, getName()));
		}
		return books;
	}

	@Override
	public List<Chapter> listChapters(String url) throws Exception {
		Document doc = Jsoup.connect(url)
				.userAgent(NetUtil.USER_AGENT)
				.get();

		Element list = doc.getElementById("list");
		Elements all = list.select("dd > a");
		final List<Chapter> chapters = new ArrayList<>();
		for (Element a : all) {
			chapters.add(new Chapter(a.text(), a.attr("abs:href")));
		}
		return chapters;
	}

	@Override
	public List<String> listContent(String chapterUrl) throws Exception {
		Document doc = Jsoup.connect(chapterUrl)
				.userAgent(NetUtil.USER_AGENT)
				.get();

		Element content = doc.getElementById("content");
		return ParseUtil.parseContentByNodes(content.textNodes());
	}

	@Override
	public String getName() {
		return SITE_NAME;
	}
}

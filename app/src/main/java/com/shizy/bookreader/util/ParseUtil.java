package com.shizy.bookreader.util;

import org.jsoup.nodes.TextNode;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {

	public static List<String> parseContentByNodes(List<TextNode> nodes) {
		final List<String> sections = new ArrayList<>();
		for (TextNode node : nodes) {
			String text = node.text();
			if (text == null || text.trim().length() == 0) {
				continue;
			}
			sections.add(text);
		}
		return sections;
	}

}

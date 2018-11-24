package com.shizy.bookreader.site;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteFactory {

	private static final List<Site> SITES = new ArrayList<>();

	static {
		SITES.add(new Binhuo());
		SITES.add(new Biquge());
	}

	private SiteFactory() {
	}

	public static List<Site> getAllSites() {
		return Collections.unmodifiableList(SITES);
	}

	public static Site getSiteByName(String name) {
		for (Site site : SITES) {
			if (TextUtils.equals(site.getName(), name)) {
				return site;
			}
		}
		return null;
	}

}

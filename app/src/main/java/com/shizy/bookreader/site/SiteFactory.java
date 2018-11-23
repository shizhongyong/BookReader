package com.shizy.bookreader.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteFactory {

	private static final List<Site> SITES = new ArrayList<>();

	static {
		SITES.add(new TestSize());
		SITES.add(new TestSize2());
	}

	private SiteFactory() {
	}

	public static List<Site> getAllSites() {
		return Collections.unmodifiableList(SITES);
	}

}

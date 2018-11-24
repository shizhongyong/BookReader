package com.shizy.bookreader.util;

/**
 * 防多次点击
 */
public class ClickUtil {

	private static int VALID_INTERVAL = 500;
	private static long sLastClickTime;

	public static boolean isValid() {
		long current = System.currentTimeMillis();
		boolean result = current - sLastClickTime > VALID_INTERVAL;
		sLastClickTime = current;
		return result;
	}

}

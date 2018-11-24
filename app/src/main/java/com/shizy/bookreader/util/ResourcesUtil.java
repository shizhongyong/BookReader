package com.shizy.bookreader.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

public class ResourcesUtil {

	private static Resources sResources = AppUtil.getResources();

	public static int getInteger(@IntegerRes int id) {
		return sResources.getInteger(id);
	}

	public static String getString(@StringRes int id) {
		return sResources.getString(id);
	}

	public static String getString(@StringRes int id, Object... formatArgs) {
		return sResources.getString(id, formatArgs);
	}

	public static int getColor(@ColorRes int id) {
		return ContextCompat.getColor(AppUtil.getContext(), id);
	}

	public static ColorStateList getColorStateList(@ColorRes int id) {
		return ContextCompat.getColorStateList(AppUtil.getContext(), id);
	}

	public static float getDimension(@DimenRes int id) {
		return sResources.getDimension(id);
	}

	public static int getDimensionPixelSize(@DimenRes int id) {
		return sResources.getDimensionPixelSize(id);
	}

	public static Drawable getDrawable(@DrawableRes int id) {
		return ContextCompat.getDrawable(AppUtil.getContext(), id);
	}

}

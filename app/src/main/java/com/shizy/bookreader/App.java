package com.shizy.bookreader;

import android.app.Application;

import com.shizy.bookreader.util.AppUtil;

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		AppUtil.init(this);
	}
}

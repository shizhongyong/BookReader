package com.shizy.bookreader.ui.base;

import com.shizy.bookreader.BuildConfig;

import io.reactivex.observers.DisposableObserver;

public abstract class BaseObserver<T> extends DisposableObserver<T> {

	@Override
	public final void onComplete() {
		if (!isDisposed()) {
			dispose();
		}
	}

	@Override
	public final void onError(Throwable e) {
		if (BuildConfig.DEBUG) {
			e.printStackTrace();
		}
		if (!isDisposed()) {
			dispose();
		}
	}

}
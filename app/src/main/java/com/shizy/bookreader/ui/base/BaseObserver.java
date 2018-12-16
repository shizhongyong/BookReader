package com.shizy.bookreader.ui.base;

import com.shizy.bookreader.BuildConfig;
import com.shizy.bookreader.util.UIUtil;

import io.reactivex.observers.DisposableObserver;

public abstract class BaseObserver<T> extends DisposableObserver<T> {

	@Override
	public final void onComplete() {
		if (!isDisposed()) {
			dispose();
		}
		onFinally();
	}

	@Override
	public final void onError(Throwable e) {
		if (BuildConfig.DEBUG) {
			e.printStackTrace();
		}
		onFailure(e);
		if (!isDisposed()) {
			dispose();
		}
		onFinally();
	}

	protected void onFailure(Throwable e) {
		UIUtil.showToast(e.getMessage());
	}

	protected void onFinally() {

	}

}
package com.shizy.bookreader.ui.base.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.shizy.bookreader.R;
import com.shizy.bookreader.util.RxJavaUtil;
import com.uber.autodispose.AutoDisposeConverter;

import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shizy on 2018/10/22.
 */
public abstract class BaseActivity extends AppCompatActivity {

	@Nullable
	@BindView(R.id.layout_loading)
	protected View mLoadingView;

	private AtomicInteger mLoadingCount = new AtomicInteger(0);

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		ButterKnife.bind(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ButterKnife.bind(this);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		ButterKnife.bind(this);
	}

	protected <T> AutoDisposeConverter<T> bindLifecycle() {
		return RxJavaUtil.bindLifecycle(this);
	}

	protected void showLoading() {
		mLoadingCount.incrementAndGet();
		if (mLoadingView != null) {
			mLoadingView.setVisibility(View.VISIBLE);
		}
	}

	protected void hideLoading() {
		if (mLoadingView != null && mLoadingCount.decrementAndGet() <= 0) {
			mLoadingView.setVisibility(View.GONE);
		}
	}

	protected boolean isLoading() {
		return mLoadingCount.get() > 0;
	}

}

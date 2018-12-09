package com.shizy.bookreader.ui.base.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shizy.bookreader.R;
import com.shizy.bookreader.util.RxJavaUtil;
import com.uber.autodispose.AutoDisposeConverter;

import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

	@Nullable
	@BindView(R.id.layout_loading)
	protected View mLoadingView;

	private AtomicInteger mLoadingCount = new AtomicInteger(0);

	protected <T> AutoDisposeConverter<T> bindLifecycle() {
		return RxJavaUtil.bindLifecycle(this);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getLayoutId() > 0) {
			View root = inflater.inflate(getLayoutId(), container, false);
			ButterKnife.bind(this, root);
			return root;
		}
		return super.onCreateView(inflater, container, savedInstanceState);
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

	protected abstract int getLayoutId();

}

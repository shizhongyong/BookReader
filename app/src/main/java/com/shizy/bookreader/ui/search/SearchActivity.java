package com.shizy.bookreader.ui.search;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.site.Site;
import com.shizy.bookreader.site.SiteFactory;
import com.shizy.bookreader.ui.base.BaseObserver;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.content.ReadActivity;
import com.shizy.bookreader.util.ClickUtil;
import com.shizy.bookreader.util.RxJavaUtil;
import com.shizy.bookreader.util.UIUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class SearchActivity extends BaseActivity {

	private static final String TAG = SearchActivity.class.getSimpleName();

	private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			mSite = (Site) parent.getAdapter().getItem(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private BaseAdapter.OnItemClickListener mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			Book book = mAdapter.getItem(position);
			if (book != null) {
				ReadActivity.launch(SearchActivity.this, book);
			}
		}
	};

	@BindView(R.id.edit_keyword)
	protected EditText mKeywordEdit;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;
	@BindView(R.id.layout_empty)
	protected ViewGroup mEmptyLayout;
	@BindView(R.id.tv_message)
	protected TextView mMessageTv;

	private Site mSite;
	private List<Site> mSites;
	private SearchAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStatusBarColor(Color.WHITE);
		setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		setContentView(R.layout.activity_search);
		ButterKnife.bind(this);

		mSites = SiteFactory.getAllSites();
		mSite = mSites.get(0);

		initView();
	}

	private void initView() {
		initEmptyMessage();
		initRecyclerView();
	}

	private void initEmptyMessage() {
		final String message = getString(R.string.search_result_empty);
		final String spanTxt = getString(R.string.change_site);
		final int start = message.indexOf(spanTxt);
		final int end = start + spanTxt.length();

		SpannableString spannableString = new SpannableString(message);
		spannableString.setSpan(new ClickableSpan() {
			@Override
			public void onClick(@NonNull View widget) {
				if (!ClickUtil.isValid()) {
					return;
				}
				showSitesDialog();
			}

			@Override
			public void updateDrawState(@NonNull TextPaint ds) {
				ds.setColor(ds.linkColor);
				ds.setUnderlineText(false);
			}
		}, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		mMessageTv.setText(spannableString);
		mMessageTv.setHighlightColor(Color.parseColor("#00000000"));
		mMessageTv.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void initRecyclerView() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new SearchAdapter(this);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mAdapter);

		RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
		mRecyclerView.addItemDecoration(divider);
	}

	private void switchContent() {
		if (mAdapter.getItemCount() > 0) {
			mRecyclerView.setVisibility(View.VISIBLE);
			mEmptyLayout.setVisibility(View.GONE);
		} else {
			mEmptyLayout.setVisibility(View.VISIBLE);
			mRecyclerView.setVisibility(View.GONE);
		}
	}

	private void showSitesDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.change_site)
				.setSingleChoiceItems(new SiteListAdapter(this, mSites, mSite), 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								mSite = mSites.get(which);
								final String keyword = mKeywordEdit.getText().toString().trim();
								if (TextUtils.isEmpty(keyword)) {
									return;
								}
								search(keyword);
							}
						})
				.show();
	}

	@OnClick(R.id.tv_cancel)
	protected void onClick(View view) {
		if (!ClickUtil.isValid()) {
			return;
		}

		UIUtil.hideSoftInput(getCurrentFocus());
		finish();
	}

	@OnEditorAction(R.id.edit_keyword)
	protected boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
		if (!ClickUtil.isValid()) {
			return false;
		}

		final String keyword = textView.getText().toString().trim();
		if (TextUtils.isEmpty(keyword)) {
			return false;
		}

		UIUtil.hideSoftInput(getCurrentFocus());
		search(keyword);
		return true;
	}

	private void search(final String keyword) {
		if (mEmptyLayout.getVisibility() == View.VISIBLE) {
			mEmptyLayout.setVisibility(View.GONE);
		}
		if (isLoading()) {
			return;
		}
		showLoading();
		Observable.create(new ObservableOnSubscribe<List<Book>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Book>> emitter) throws Exception {
				emitter.onNext(mSite.search(keyword));
				emitter.onComplete();
			}
		})
				.compose(RxJavaUtil.<List<Book>>mainSchedulers())
				.as(this.<List<Book>>bindLifecycle())
				.subscribe(new BaseObserver<List<Book>>() {
					@Override
					public void onNext(List<Book> books) {
						mAdapter.setData(books);
					}

					@Override
					protected void onFinally() {
						hideLoading();
						switchContent();
					}
				});
	}

}

package com.shizy.bookreader.ui.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Chapter;
import com.shizy.bookreader.site.Site;
import com.shizy.bookreader.site.SiteFactory;
import com.shizy.bookreader.ui.base.BaseObserver;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.util.ClickUtil;
import com.shizy.bookreader.util.RxJavaUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ContentActivity extends BaseActivity {

	private static final String KEY_BOOK = "book";

	private BaseAdapter.OnItemClickListener mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			Chapter chapter = mChapterAdapter.getItem(position);
			listContent(chapter);
			mDrawerLayout.closeDrawer(Gravity.END);
		}
	};

	@BindView(R.id.drawer_layout)
	protected DrawerLayout mDrawerLayout;
	@BindView(R.id.tv_content)
	protected TextView mContentTv;
	@BindView(R.id.tv_order)
	protected TextView mOrderTv;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	private ChapterAdapter mChapterAdapter;

	private Book mBook;
	private Site mSite;
	private List<Chapter> mChapters;

	private boolean isAsc = true;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content);
		mBook = (Book) getIntent().getSerializableExtra(KEY_BOOK);
		if (mBook == null) {
			finish();
			return;
		}

		mSite = SiteFactory.getSiteByName(mBook.getSiteName());
		if (mSite == null) {
			finish();
			return;
		}

		initView();
		listChapters();
	}

	private void initView() {
		initCatalogView();
	}

	private void initCatalogView() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mChapterAdapter = new ChapterAdapter(this);
		mChapterAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mChapterAdapter);
	}

	@OnClick(R.id.tv_order)
	protected void onClick(View view) {
		if (!ClickUtil.isValid()) {
			return;
		}
		switch (view.getId()) {
			case R.id.tv_order:
				isAsc = !isAsc;
				updateCatalog();
				break;
		}
	}

	private void updateCatalog() {
		if (mChapters == null) {
			return;
		}
		if (isAsc) {
			mOrderTv.setText(R.string.desc);
			mChapterAdapter.setData(mChapters);
		} else {
			mOrderTv.setText(R.string.asc);
			List<Chapter> descList = new ArrayList<>(mChapters);
			Collections.reverse(descList);
			mChapterAdapter.setData(descList);
		}
		mRecyclerView.scrollToPosition(0);
	}

	private void listChapters() {
		Observable.create(new ObservableOnSubscribe<List<Chapter>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Chapter>> emitter) throws Exception {
				emitter.onNext(mSite.listChapters(mBook.getUrl()));
				emitter.onComplete();
			}
		})
				.compose(RxJavaUtil.<List<Chapter>>mainSchedulers())
				.as(this.<List<Chapter>>bindLifecycle())
				.subscribe(new BaseObserver<List<Chapter>>() {
					@Override
					public void onNext(List<Chapter> chapters) {
						mChapters = chapters;
						updateCatalog();
					}
				});
	}

	private void listContent(final Chapter chapter) {
		if (chapter == null) {
			return;
		}
		Observable.create(new ObservableOnSubscribe<List<String>>() {
			@Override
			public void subscribe(ObservableEmitter<List<String>> emitter) throws Exception {
				emitter.onNext(mSite.listContent(chapter.getUrl()));
				emitter.onComplete();
			}
		})
				.compose(RxJavaUtil.<List<String>>mainSchedulers())
				.as(this.<List<String>>bindLifecycle())
				.subscribe(new BaseObserver<List<String>>() {
					@Override
					public void onNext(List<String> strings) {
						StringBuilder builder = new StringBuilder();
						for (String string : strings) {
							builder.append("\t\t").append(string).append("\n");
						}
						mContentTv.setText(builder);
					}
				});
	}

	public static void launch(Activity activity, Book book) {
		if (activity == null || book == null) {
			return;
		}
		Intent intent = new Intent(activity, ContentActivity.class);
		intent.putExtra(KEY_BOOK, book);
		activity.startActivity(intent);
	}

}

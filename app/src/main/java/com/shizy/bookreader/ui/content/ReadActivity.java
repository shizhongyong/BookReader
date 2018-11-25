package com.shizy.bookreader.ui.content;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.shizy.bookreader.util.ScreenUtil;
import com.shizy.bookreader.util.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ReadActivity extends BaseActivity {

	private static final int FONT_SIZE_DEFAULT = 18;
	private static final int FONT_SIZE_MIN = 14;
	private static final int FONT_SIZE_MAX = 24;

	private static final String KEY_BOOK = "book";

	private BaseAdapter.OnItemClickListener mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			Chapter chapter = mChapterAdapter.getItem(position);
			listContent(chapter);
			mDrawerLayout.closeDrawer(Gravity.END);
		}
	};

	private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
				case R.id.rb_read_1:
					mContentLayout.setBackgroundResource(R.drawable.bg_read_1);
					break;
				case R.id.rb_read_2:
					mContentLayout.setBackgroundResource(R.drawable.bg_read_2);
					break;
			}
		}
	};

	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			mGestureDetector.onTouchEvent(event);
			return false;
		}
	};

	private GestureDetector.OnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if (!ClickUtil.isValid()) {
				return true;
			}
			if (mButtonLayout.getVisibility() == View.GONE) {
				final int downX = (int) e.getX();
				final int splitWidth = ScreenUtil.screenWidth() / 3;
				if (downX < splitWidth) {
					previousChapter();
				} else if (downX > splitWidth * 2) {
					nextChapter();
				} else {
					mTopLayout.setVisibility(View.VISIBLE);
					mButtonLayout.setVisibility(View.VISIBLE);
				}
			} else {
				mTopLayout.setVisibility(View.GONE);
				mButtonLayout.setVisibility(View.GONE);
			}
			return true;
		}
	};

	@BindView(R.id.drawer_layout)
	protected DrawerLayout mDrawerLayout;
	@BindView(R.id.content_layout)
	protected FrameLayout mContentLayout;
	@BindView(R.id.top_layout)
	protected ViewGroup mTopLayout;
	@BindView(R.id.tv_title)
	protected TextView mTitleTv;
	@BindView(R.id.bottom_layout)
	protected ViewGroup mButtonLayout;
	@BindView(R.id.tv_font_size)
	protected TextView mFontSizeTv;
	@BindView(R.id.radioGroup)
	protected RadioGroup mRadioGroup;
	@BindView(R.id.scrollView)
	protected ScrollView mScrollView;
	@BindView(R.id.tv_content)
	protected TextView mContentTv;
	@BindView(R.id.tv_order)
	protected TextView mOrderTv;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	private GestureDetector mGestureDetector;

	private ChapterAdapter mChapterAdapter;

	private Book mBook;
	private Site mSite;
	private List<Chapter> mChapters;

	private boolean isAsc = true;
	private int mChapterIndex = 0;
	private int mFontSize = FONT_SIZE_DEFAULT;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);
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
		mTitleTv.setText(mBook.getName());

		mGestureDetector = new GestureDetector(this, mOnGestureListener);
		mScrollView.setOnTouchListener(mOnTouchListener);
		updateFontSize();

		mFontSizeTv.setText(String.valueOf(mFontSize));
		mRadioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mRadioGroup.check(R.id.rb_read_1);

		initCatalogView();
	}

	private void initCatalogView() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mChapterAdapter = new ChapterAdapter(this);
		mChapterAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mChapterAdapter);
	}

	@OnClick({R.id.iv_back, R.id.btn_decrease, R.id.btn_increase, R.id.tv_order})
	protected void onClick(View view) {
		if (!ClickUtil.isValid()) {
			return;
		}
		switch (view.getId()) {
			case R.id.iv_back:
				onBackPressed();
				break;
			case R.id.btn_decrease:
				mFontSize = Math.max(mFontSize - 1, FONT_SIZE_MIN);
				updateFontSize();
				break;
			case R.id.btn_increase:
				mFontSize = Math.min(mFontSize + 1, FONT_SIZE_MAX);
				updateFontSize();
				break;
			case R.id.tv_order:
				isAsc = !isAsc;
				updateCatalog();
				break;
		}
	}

	private void updateFontSize() {
		mContentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mFontSize);
		mFontSizeTv.setText(String.valueOf(mFontSize));
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

	private void openChapter(int index) {
		if (mChapters == null || mChapters.size() == 0) {
			return;
		}
		listContent(mChapters.get(index));
		mChapterIndex = index;
	}

	private void previousChapter() {
		if (mChapters == null || mChapters.size() == 0) {
			return;
		}
		if (mChapterIndex == 0) {
			UIUtil.showToast(R.string.msg_first_chapter);
			return;
		}
		openChapter(mChapterIndex - 1);
	}

	private void nextChapter() {
		if (mChapters == null || mChapters.size() == 0) {
			return;
		}
		if (mChapterIndex >= mChapters.size() - 1) {
			UIUtil.showToast(R.string.msg_last_chapter);
			return;
		}
		openChapter(mChapterIndex + 1);
	}

	private void setChapterContent(Chapter chapter, List<String> list) {
		StringBuilder builder = new StringBuilder();
		for (String item : list) {
			builder.append("\t\t").append(item).append("\n");
		}
		mContentTv.setText(builder);
		mScrollView.scrollTo(0, 0);
		mTitleTv.setText(chapter.getName());
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
						openChapter(mChapterIndex);
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
					public void onNext(List<String> list) {
						setChapterContent(chapter, list);
					}
				});
	}

	public static void launch(Activity activity, Book book) {
		if (activity == null || book == null) {
			return;
		}
		Intent intent = new Intent(activity, ReadActivity.class);
		intent.putExtra(KEY_BOOK, book);
		activity.startActivity(intent);
	}

}

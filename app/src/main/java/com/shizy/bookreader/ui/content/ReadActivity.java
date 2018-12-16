package com.shizy.bookreader.ui.content;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.bean.Chapter;
import com.shizy.bookreader.db.DatabaseHelper;
import com.shizy.bookreader.db.dao.BookDao;
import com.shizy.bookreader.site.Site;
import com.shizy.bookreader.site.SiteFactory;
import com.shizy.bookreader.ui.base.BaseObserver;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.search.SiteListAdapter;
import com.shizy.bookreader.util.ClickUtil;
import com.shizy.bookreader.util.RxJavaUtil;
import com.shizy.bookreader.util.ScreenUtil;
import com.shizy.bookreader.util.UIUtil;

import java.sql.SQLException;
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
			// position有正序和倒序之分
			final int index = mChapters.indexOf(mChapterAdapter.getItem(position));
			readChapter(index);
			mDrawerLayout.closeDrawer(Gravity.START);
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
				final int splitWidth = ScreenUtil.screenWidth() / 9;// 左：中：右 === 2：3：4
				if (downX < splitWidth * 2) {
					previousChapter();
				} else if (downX > splitWidth * 5) {
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
	@BindView(R.id.catalog)
	protected ViewGroup mCatalogLayout;
	@BindView(R.id.tv_order)
	protected TextView mOrderTv;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	@BindView(R.id.layout_failed)
	protected ViewGroup mFailedLayout;

	private GestureDetector mGestureDetector;

	private ChapterAdapter mChapterAdapter;

	private Book mBook;
	private Site mSite;
	private List<Chapter> mChapters;

	private BookDao mBookDao;

	private boolean isAsc = true;
	private int mReadChapterIndex = 0;
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

		try {
			mBookDao = DatabaseHelper.getHelper(this).getBookDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		initView();
		listChapters();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DatabaseHelper.releaseHelper();
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
		ViewGroup.LayoutParams params = mCatalogLayout.getLayoutParams();
		params.width = (int) (ScreenUtil.screenWidth() * 0.68);
		mCatalogLayout.setLayoutParams(params);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mChapterAdapter = new ChapterAdapter(this);
		mChapterAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mChapterAdapter);
	}

	@OnClick({R.id.iv_back, R.id.btn_decrease, R.id.btn_increase, R.id.tv_order, R.id.tv_retry, R.id.tv_change_source})
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
			case R.id.tv_retry:
				retry();
				break;
			case R.id.tv_change_source:
				showSitesDialog();
				break;
		}
	}

	private void showFailedView(boolean show) {
		mFailedLayout.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void retry() {
		showFailedView(false);
		if (mChapters == null || mChapters.isEmpty()) {
			listChapters();
		} else {
			readChapter(mReadChapterIndex);
		}
	}

	private void showSitesDialog() {
		final List<Site> sites = SiteFactory.getAllSites();
		new AlertDialog.Builder(this)
				.setTitle(R.string.change_site)
				.setSingleChoiceItems(new SiteListAdapter(this, sites, mSite), 0,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								final Site site = sites.get(which);
								if (mSite.equals(site)) {
									return;
								}

								changeSite(site);
							}
						})
				.show();
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

	private void readChapter(int index) {
		if (mChapters == null || mChapters.size() == 0 || mChapters.size() <= index) {
			return;
		}
		listContent(mChapters.get(index), index);
	}

	private void previousChapter() {
		if (mChapters == null || mChapters.size() == 0) {
			return;
		}
		if (mReadChapterIndex == 0) {
			UIUtil.showToast(R.string.msg_first_chapter);
			return;
		}
		readChapter(mReadChapterIndex - 1);
	}

	private void nextChapter() {
		if (mChapters == null || mChapters.size() == 0) {
			return;
		}
		if (mReadChapterIndex >= mChapters.size() - 1) {
			UIUtil.showToast(R.string.msg_last_chapter);
			return;
		}
		readChapter(mReadChapterIndex + 1);
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

	private void changeSite(final Site site) {
		showLoading();
		Observable.create(new ObservableOnSubscribe<List<Book>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Book>> emitter) throws Exception {
				emitter.onNext(site.search(mBook.getName()));
				emitter.onComplete();
			}
		})
				.compose(RxJavaUtil.<List<Book>>mainSchedulers())
				.as(this.<List<Book>>bindLifecycle())
				.subscribe(new BaseObserver<List<Book>>() {
					@Override
					public void onNext(List<Book> books) {
						Book bookInNewSite = null;
						if (books != null && !books.isEmpty()) {
							for (Book book : books) {
								if (TextUtils.equals(book.getName(), mBook.getName()) && TextUtils.equals(book.getAuthor(), mBook.getAuthor())) {
									bookInNewSite = book;
									break;
								}
							}
						}
						if (bookInNewSite != null) {
							updateBookSite(bookInNewSite);
							mBook = bookInNewSite;
							retry();
						} else {
							notFound();
						}
					}

					@Override
					protected void onFailure(Throwable e) {
						notFound();
					}

					private void notFound() {
						UIUtil.showToast(getString(R.string.format_not_found_book, mBook.getName()));
					}

					@Override
					protected void onFinally() {
						hideLoading();
					}
				});
	}

	private void listChapters() {
		showLoading();
		Observable.create(new ObservableOnSubscribe<List<Chapter>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Chapter>> emitter) throws Exception {
				if (mBookDao != null) {
					mReadChapterIndex = mBookDao.queryReadChapter(mBook);
				}
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
						readChapter(mReadChapterIndex);
					}

					@Override
					protected void onFailure(Throwable e) {
						super.onFailure(e);
						showFailedView(true);
					}

					@Override
					protected void onFinally() {
						hideLoading();
					}
				});
	}

	private void listContent(final Chapter chapter, final int index) {
		if (chapter == null) {
			return;
		}
		showLoading();
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
						mReadChapterIndex = index;
						mChapterAdapter.setCurrentChapter(chapter);
						updateReadChapter();
					}

					@Override
					protected void onFailure(Throwable e) {
						super.onFailure(e);
						mContentTv.setText(null);
						showFailedView(true);
					}

					@Override
					protected void onFinally() {
						hideLoading();
					}
				});
	}

	private void updateReadChapter() {
		Observable.create(new ObservableOnSubscribe<Boolean>() {
			@Override
			public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
				if (mBookDao != null) {
					emitter.onNext(mBookDao.updateReadChapter(mBook, mReadChapterIndex));
					emitter.onComplete();
				} else {
					emitter.onError(new NullPointerException("mBookDao is null!"));
				}
			}
		})
				.compose(RxJavaUtil.<Boolean>ioSchedulers())
				.as(this.<Boolean>bindLifecycle())
				.subscribe();
	}

	private void updateBookSite(final Book book) {
		Observable.create(new ObservableOnSubscribe<Boolean>() {
			@Override
			public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
				if (mBookDao != null) {
					emitter.onNext(mBookDao.updateBookSite(book));
					emitter.onComplete();
				} else {
					emitter.onError(new NullPointerException("mBookDao is null!"));
				}
			}
		})
				.compose(RxJavaUtil.<Boolean>ioSchedulers())
				.as(this.<Boolean>bindLifecycle())
				.subscribe();
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

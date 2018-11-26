package com.shizy.bookreader.ui.search;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.site.Site;
import com.shizy.bookreader.site.SiteFactory;
import com.shizy.bookreader.ui.base.BaseObserver;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.content.ReadActivity;
import com.shizy.bookreader.util.RxJavaUtil;
import com.shizy.bookreader.util.UIUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

	@BindView(R.id.iv_back)
	protected ImageView mBackIv;
	@BindView(R.id.spinner)
	protected Spinner mSpinner;
	@BindView(R.id.search_view)
	protected SearchView mSearchView;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	private Site mSite;
	private SearchAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ButterKnife.bind(this);

		initView();
	}

	private void initView() {
		initSpinner();
		initSearchView();
		initRecyclerView();
	}

	private void initSpinner() {
		final List<Site> sites = SiteFactory.getAllSites();
		ArrayAdapter<Site> adapter = new ArrayAdapter<>(this, R.layout.item_site_spinner);
		adapter.addAll(sites);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(mOnItemSelectedListener);
		mSpinner.setDropDownVerticalOffset(getResources().getDimensionPixelSize(R.dimen.site_dropdown_offset));
		mSpinner.setDropDownWidth(getResources().getDimensionPixelSize(R.dimen.site_dropdown_width));

		mSite = sites.get(0);
	}

	private void initSearchView() {
		mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				UIUtil.hideSoftInput(getCurrentFocus());
				search(s);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return true;
			}
		});
	}

	private void initRecyclerView() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new SearchAdapter(this);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mAdapter);

		RecyclerView.ItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
		mRecyclerView.addItemDecoration(divider);
	}

	@OnClick(R.id.iv_back)
	protected void onClick(View view) {
		onBackPressed();
	}

	private void search(final String keyword) {
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
					}
				});
	}

}

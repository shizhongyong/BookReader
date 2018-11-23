package com.shizy.bookreader.ui.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends AppCompatActivity {

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
		mRecyclerView.setAdapter(mAdapter);

		final List<Book> books = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			Book book = new Book();
			book.setName(String.valueOf(i));
			books.add(book);
		}
		mAdapter.addAll(books);
	}

	@OnClick(R.id.iv_back)
	protected void onClick(View view) {
		onBackPressed();
	}

	private void search(String keyword) {

	}

}

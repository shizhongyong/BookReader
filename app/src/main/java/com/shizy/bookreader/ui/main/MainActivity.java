package com.shizy.bookreader.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.db.DatabaseHelper;
import com.shizy.bookreader.db.dao.BookDao;
import com.shizy.bookreader.ui.base.BaseObserver;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.content.ReadActivity;
import com.shizy.bookreader.ui.search.SearchActivity;
import com.shizy.bookreader.util.ResourcesUtil;
import com.shizy.bookreader.util.RxJavaUtil;
import com.shizy.bookreader.util.UIUtil;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class MainActivity extends BaseActivity {

	private static final long EXIT_INTERVAL = 2000;

	private NavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.nav_camera:
					break;
				case R.id.nav_gallery:
					break;
				case R.id.nav_slideshow:
					break;
				case R.id.nav_manage:
					break;
				case R.id.nav_share:
					break;
				case R.id.nav_send:
					break;
			}

			mDrawerLayout.closeDrawer(GravityCompat.START);
			return true;
		}
	};

	private BaseAdapter.OnItemClickListener mOnItemClickListener = new BaseAdapter.OnItemClickListener() {
		@Override
		public void onItemClick(View view, int position) {
			Book book = mAdapter.getItem(position);
			if (book != null) {
				ReadActivity.launch(MainActivity.this, book);
			}
		}
	};

	private MainAdapter.ActionListener mActionListener = new MainAdapter.ActionListener() {
		@Override
		public void removeBook(Book book) {
			showRemoveDialog(book);
		}
	};

	@BindView(R.id.drawer_layout)
	protected DrawerLayout mDrawerLayout;
	@BindView(R.id.nav_view)
	protected NavigationView mNavView;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	private MainAdapter mAdapter;
	private long mLastBackTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadData();
	}

	private void initView() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		mDrawerLayout.addDrawerListener(toggle);
		toggle.syncState();

		mNavView.setNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

		initRecyclerView();
	}

	private void initRecyclerView() {
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mAdapter = new MainAdapter(this, mActionListener);
		mAdapter.setOnItemClickListener(mOnItemClickListener);
		mRecyclerView.setAdapter(mAdapter);
	}

	@OnClick(R.id.fab)
	protected void onClick(View view) {
		startActivity(new Intent(MainActivity.this, SearchActivity.class));
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			if (System.currentTimeMillis() - mLastBackTime > EXIT_INTERVAL) {
				UIUtil.showToast(R.string.msg_exit_app);
				mLastBackTime = System.currentTimeMillis();
			} else {
				super.onBackPressed();
			}
		}
	}

	private void showRemoveDialog(final Book book) {
		new AlertDialog.Builder(this)
				.setMessage(ResourcesUtil.getString(R.string.format_remove_confirm, book.getName()))
				.setNegativeButton(R.string.cancel, null)
				.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						removeBook(book);
					}
				})
				.show();
	}

	private void removeBook(Book book) {
		try {
			BookDao dao = DatabaseHelper.getHelper(this).getBookDao();
			dao.delete(book);
			mAdapter.remove(book);
			UIUtil.showToast(ResourcesUtil.getString(R.string.format_remove_from_bookshelves, book.getName()));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseHelper.releaseHelper();
		}
	}

	private void loadData() {
		Observable.create(new ObservableOnSubscribe<List<Book>>() {
			@Override
			public void subscribe(ObservableEmitter<List<Book>> emitter) throws Exception {
				try {
					BookDao dao = DatabaseHelper.getHelper(MainActivity.this).getBookDao();
					List<Book> list = dao.queryForAll();
					Collections.reverse(list);
					emitter.onNext(list);
					emitter.onComplete();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					DatabaseHelper.releaseHelper();
				}
			}
		})
				.compose(RxJavaUtil.<List<Book>>mainSchedulers())
				.as(this.<List<Book>>bindLifecycle())
				.subscribe(new BaseObserver<List<Book>>() {
					@Override
					public void onNext(List<Book> books) {
						mAdapter.setData(books);
					}
				});

	}

}

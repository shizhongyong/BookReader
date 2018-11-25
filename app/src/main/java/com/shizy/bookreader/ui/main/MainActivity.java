package com.shizy.bookreader.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.ui.base.activity.BaseActivity;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.content.ReadActivity;
import com.shizy.bookreader.ui.search.SearchActivity;
import com.shizy.bookreader.ui.search.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

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

	@BindView(R.id.drawer_layout)
	protected DrawerLayout mDrawerLayout;
	@BindView(R.id.nav_view)
	protected NavigationView mNavView;
	@BindView(R.id.recycler_view)
	protected RecyclerView mRecyclerView;

	private MainAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
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
		mAdapter = new MainAdapter(this);
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
			super.onBackPressed();
		}
	}

}

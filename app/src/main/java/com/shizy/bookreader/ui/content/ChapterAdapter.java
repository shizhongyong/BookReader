package com.shizy.bookreader.ui.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Chapter;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.base.adapter.BaseViewHolder;

import butterknife.BindView;

public class ChapterAdapter extends BaseAdapter<Chapter, ChapterAdapter.ChapterViewHolder> {

	private Chapter mCurrentChapter = null;
	private LinearLayoutManager mLayoutManager = null;

	public ChapterAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_list, parent, false);
		return new ChapterViewHolder(this, view);
	}

	@Override
	public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
			mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
		}
	}

	@Override
	public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);
		mLayoutManager = null;
	}

	public void setCurrentChapter(Chapter chapter) {
		this.mCurrentChapter = chapter;
		if (mLayoutManager != null) {
			final int first = mLayoutManager.findFirstVisibleItemPosition();
			final int last = mLayoutManager.findLastVisibleItemPosition();
			final int position = mData.indexOf(chapter);
			if (position < first || position > last) {
				mLayoutManager.scrollToPosition(position);
			}
		}
		notifyDataSetChanged();
	}

	class ChapterViewHolder extends BaseViewHolder<Chapter> {

		@BindView(R.id.text)
		protected CheckedTextView mTextView;

		public ChapterViewHolder(BaseAdapter adapter, View itemView) {
			super(adapter, itemView);
		}

		@Override
		public void bindData(Chapter chapter) {
			mTextView.setText(chapter.getName());
			mTextView.setChecked(mCurrentChapter == chapter);
		}
	}

}

package com.shizy.bookreader.ui.content;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Chapter;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.base.adapter.BaseViewHolder;

import butterknife.BindView;

public class ChapterAdapter extends BaseAdapter<Chapter, ChapterAdapter.ChapterViewHolder> {

	public ChapterAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter_list, parent, false);
		return new ChapterViewHolder(this, view);
	}

	class ChapterViewHolder extends BaseViewHolder<Chapter> {

		@BindView(R.id.text)
		protected TextView mTextView;

		public ChapterViewHolder(BaseAdapter adapter, View itemView) {
			super(adapter, itemView);
		}

		@Override
		public void bindData(Chapter chapter) {
			mTextView.setText(chapter.getName());
		}
	}

}

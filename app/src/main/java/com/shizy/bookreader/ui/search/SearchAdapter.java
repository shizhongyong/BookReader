package com.shizy.bookreader.ui.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.base.adapter.BaseViewHolder;

import butterknife.BindView;

public class SearchAdapter extends BaseAdapter<Book, SearchAdapter.ItemViewHolder> {

	public SearchAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
		return new ItemViewHolder(this, view);
	}

	class ItemViewHolder extends BaseViewHolder<Book> {

		@BindView(android.R.id.text1)
		protected TextView mTextView;

		public ItemViewHolder(BaseAdapter adapter, View itemView) {
			super(adapter, itemView);
		}

		@Override
		public void bindData(Book data) {
			mTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
			mTextView.setText(data.getName());
		}
	}

}

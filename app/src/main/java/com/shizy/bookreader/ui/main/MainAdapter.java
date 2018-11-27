package com.shizy.bookreader.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shizy.bookreader.R;
import com.shizy.bookreader.bean.Book;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.base.adapter.BaseViewHolder;

import butterknife.BindView;

public class MainAdapter extends BaseAdapter<Book, MainAdapter.ItemViewHolder> {

	public interface ActionListener {

		void removeBook(Book book);

	}

	private ActionListener mActionListener;

	public MainAdapter(Context context, ActionListener listener) {
		super(context);
		mActionListener = listener;
	}

	@NonNull
	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_list, parent, false);
		return new MainAdapter.ItemViewHolder(this, view);
	}

	class ItemViewHolder extends BaseViewHolder<Book> {

		@BindView(R.id.iv_poster)
		protected ImageView mPosterIv;
		@BindView(R.id.tv_name)
		protected TextView mNameTv;
		@BindView(R.id.tv_author)
		protected TextView mAuthorTv;
		@BindView(R.id.tv_latestChapter)
		protected TextView mLatestChapterTv;
		@BindView(R.id.iv_delete)
		protected ImageView mDeleteIv;

		public ItemViewHolder(BaseAdapter adapter, View itemView) {
			super(adapter, itemView);
		}

		@Override
		public void bindData(final Book book) {
			if (!TextUtils.isEmpty(book.getPoster())) {
				Glide.with(mContext).load(book.getPoster()).into(mPosterIv);
			} else {
				mPosterIv.setImageResource(R.drawable.poster_default);
			}

			mNameTv.setText(book.getName());
			mAuthorTv.setText(book.getAuthor());
			mLatestChapterTv.setText(book.getLatestChapter());

			mDeleteIv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mActionListener != null) {
						mActionListener.removeBook(book);
					}
				}
			});
		}

	}
}

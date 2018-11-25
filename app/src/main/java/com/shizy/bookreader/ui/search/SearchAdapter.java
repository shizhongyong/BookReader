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
import com.shizy.bookreader.db.DatabaseHelper;
import com.shizy.bookreader.db.dao.BookDao;
import com.shizy.bookreader.ui.base.adapter.BaseAdapter;
import com.shizy.bookreader.ui.base.adapter.BaseViewHolder;
import com.shizy.bookreader.util.ResourcesUtil;
import com.shizy.bookreader.util.UIUtil;

import java.sql.SQLException;

import butterknife.BindView;

public class SearchAdapter extends BaseAdapter<Book, SearchAdapter.ItemViewHolder> {

	public SearchAdapter(Context context) {
		super(context);
	}

	@NonNull
	@Override
	public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
		return new ItemViewHolder(this, view);
	}

	class ItemViewHolder extends BaseViewHolder<Book> {

		@BindView(R.id.tv_name)
		protected TextView mNameTv;
		@BindView(R.id.tv_author)
		protected TextView mAuthorTv;
		@BindView(R.id.tv_latestChapter)
		protected TextView mLatestChapterTv;
		@BindView(R.id.tv_add)
		protected TextView mAddTv;

		public ItemViewHolder(BaseAdapter adapter, View itemView) {
			super(adapter, itemView);
		}

		@Override
		public void bindData(final Book book) {
			mNameTv.setText(book.getName());
			mAuthorTv.setText(ResourcesUtil.getString(R.string.format_author, book.getAuthor()));
			mLatestChapterTv.setText(ResourcesUtil.getString(R.string.format_latest_chapter, book.getLatestChapter()));

			mAddTv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					saveBook(book);
				}
			});
		}

		private void saveBook(Book book) {
			try {
				BookDao dao = DatabaseHelper.getHelper(mContext).getBookDao();
				dao.createIfNotExists(book);
				UIUtil.showToast(ResourcesUtil.getString(R.string.format_add_to_bookshelves, book.getName()));
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DatabaseHelper.releaseHelper();
			}
		}

	}

}

package com.shizy.bookreader.ui.base.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> implements View.OnClickListener {

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	protected final Context mContext;
	protected final List<T> mData;
	private OnItemClickListener mOnItemClickListener;

	public BaseAdapter(Context context) {
		this(context, null);
	}

	public BaseAdapter(Context context, List<T> data) {
		mContext = context;
		mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	@Override
	public void onBindViewHolder(@NonNull VH holder, int position) {
		holder.bindData(mData.get(position));
		holder.itemView.setTag(position);
	}

	@Override
	public void onClick(View view) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
		}
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public T getItem(int position) {
		if (position < 0 || position >= getItemCount()) {
			return null;
		}
		return mData.get(position);
	}

	public void setData(List<T> data) {
		mData.clear();
		addAll(data);
	}

	public void add(T item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addAll(List<T> items) {
		mData.addAll(items);
		notifyDataSetChanged();
	}

	public void remove(T item) {
		mData.remove(item);
		notifyDataSetChanged();
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

}

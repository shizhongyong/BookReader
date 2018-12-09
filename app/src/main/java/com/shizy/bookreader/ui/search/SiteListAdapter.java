package com.shizy.bookreader.ui.search;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shizy.bookreader.R;
import com.shizy.bookreader.site.Site;

import java.util.List;

public class SiteListAdapter extends BaseAdapter {

	private List<Site> mSites;
	private Context mContext;
	private Site mSelectedSite;

	public SiteListAdapter(Context context, List<Site> sites, Site selected) {
		mContext = context;
		mSites = sites;
		mSelectedSite = selected;
	}

	@Override
	public int getCount() {
		return mSites.size();
	}

	@Override
	public Object getItem(int position) {
		return mSites.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_site_list, parent, false);
			holder = new ViewHolder(textView);
			textView.setTag(holder);

			convertView = textView;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.bindData(mSites.get(position));
		return convertView;
	}

	class ViewHolder {

		private TextView mTextView;
		private int mNormalColor;
		private int mSelectedColor;

		public ViewHolder(TextView textView) {
			mTextView = textView;
			mNormalColor = Color.parseColor("#666666");
			mSelectedColor = Color.parseColor("#333333");
		}

		private void bindData(Site site) {
			if (site == null) {
				return;
			}

			mTextView.setText(site.getName());
			if (isSelected(site)) {
				mTextView.setTextColor(mSelectedColor);
			} else {
				mTextView.setTextColor(mNormalColor);
			}
		}

		private boolean isSelected(Site site) {
			if (mSelectedSite == null || site == null) {
				return false;
			}
			return TextUtils.equals(site.getName(), mSelectedSite.getName());
		}

	}

}

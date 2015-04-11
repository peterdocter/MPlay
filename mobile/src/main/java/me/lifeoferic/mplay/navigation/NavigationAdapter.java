package me.lifeoferic.mplay.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.lifeoferic.mplay.R;

/**
 * Adapter for Navigation View
 */
public class NavigationAdapter extends BaseAdapter {

	private static ArrayList<Integer> mIconArrayList;
	private static ArrayList<Integer> mTextArrayList;
	private LayoutInflater mInflater;
	private Context mContext;

	static {
		mIconArrayList = new ArrayList<>();
		mIconArrayList.add(R.drawable.ic_action_android);
		mIconArrayList.add(R.drawable.ic_action_android);

		mTextArrayList = new ArrayList<>();
		mTextArrayList.add(R.string.navigation_option_1);
		mTextArrayList.add(R.string.navigation_option_2);
	}

	public NavigationAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mTextArrayList.size();
	}

	@Override
	public String getItem(int i) {
		return mContext.getString(mTextArrayList.get(i));
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View convertView, ViewGroup viewGroup) {
		NavigationViewHolder holder;
		if (convertView == null) {
			holder = new NavigationViewHolder();
			convertView = mInflater.inflate(R.layout.navivation_list_item, viewGroup, false);
			holder.iconImageView = (ImageView) convertView.findViewById(R.id.navigation_icon);
			holder.optionTextView = (TextView) convertView.findViewById(R.id.navigation_text);
			convertView.setTag(holder);
		} else {
			holder = (NavigationViewHolder) convertView.getTag();
		}

		holder.iconImageView.setImageResource(mIconArrayList.get(i));
		holder.optionTextView.setText(mTextArrayList.get(i));

		return convertView;
	}

	class NavigationViewHolder {
		ImageView iconImageView;
		TextView optionTextView;
	}
}

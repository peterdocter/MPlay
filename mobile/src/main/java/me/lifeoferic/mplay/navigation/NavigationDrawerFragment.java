package me.lifeoferic.mplay.navigation;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.navigation.NavigationAdapter;

/**
 * Created by socheong on 4/10/15.
 */
public class NavigationDrawerFragment extends Fragment {

	private ListView mListView;

	public NavigationDrawerFragment() {

	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation, container, false);
		mListView = (ListView) view.findViewById(R.id.navigation_option_listview);
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		NavigationAdapter adapter = new NavigationAdapter(getActivity());
		mListView.setAdapter(adapter);
	}
}

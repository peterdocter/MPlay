package me.lifeoferic.mplay.navigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.musiclist.LibraryFragment;
import me.lifeoferic.mplay.about.AboutFragment;
import me.lifeoferic.mplay.settings.SettingsFragment;

/**
 * Created by socheong on 4/10/15.
 */
public class NavigationDrawerFragment extends MPlayFragment {

	private ListView mListView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_navigation, container, false);
		mListView = (ListView) view.findViewById(R.id.navigation_option_listview);

		setListeners();

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		NavigationAdapter adapter = new NavigationAdapter(getActivity());
		mListView.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setListeners() {
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				switch(i) {
					case 0:
						getMainActivity().openFragment(LibraryFragment.newInstance());
						break;
					case 1:
						getMainActivity().openFragment(LibraryFragment.newInstance());
						break;
					case 2:
						getMainActivity().openFragment(SettingsFragment.newInstance());
						break;
					case 3:
						getMainActivity().openFragment(AboutFragment.newInstance());
						break;
				}
			}
		});
	}
}

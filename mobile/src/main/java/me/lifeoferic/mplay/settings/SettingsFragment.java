package me.lifeoferic.mplay.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;

/**
 * Created by socheong on 4/25/15.
 */
public class SettingsFragment extends MPlayFragment {

	public static SettingsFragment newInstance() {
		return new SettingsFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		return rootView;
	}
}

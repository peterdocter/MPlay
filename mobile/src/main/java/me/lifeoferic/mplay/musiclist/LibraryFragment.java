package me.lifeoferic.mplay.musiclist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.lifeoferic.mplay.MPlayFragment;

/**
 * Created by socheong on 4/26/15.
 */
public class LibraryFragment extends MPlayFragment {

	public static LibraryFragment newInstance() {
		return new LibraryFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}


}

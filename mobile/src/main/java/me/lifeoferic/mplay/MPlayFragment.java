package me.lifeoferic.mplay;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Architecture App Fragment
 */
public class MPlayFragment extends Fragment {

	protected static final String TITLE = "";

	public MPlayFragment() {
		setArguments(new Bundle());
	}

	protected MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}

	@Override
	public void onResume() {
		super.onResume();
		getMainActivity().setTitle(TITLE);
	}
}

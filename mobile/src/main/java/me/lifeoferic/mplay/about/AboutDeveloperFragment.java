package me.lifeoferic.mplay.about;

import me.lifeoferic.mplay.MPlayFragment;

/**
 * Created by socheong on 4/29/15.
 */
public class AboutDeveloperFragment extends MPlayFragment {

	protected static final String TITLE = "About Developer";

	public static AboutDeveloperFragment newInstance() {
		return new AboutDeveloperFragment();
	}

	@Override
	public void onResume() {
		super.onResume();
		getMainActivity().setTitle(TITLE);
	}
}
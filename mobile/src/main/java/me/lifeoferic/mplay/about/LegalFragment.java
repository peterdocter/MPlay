package me.lifeoferic.mplay.about;

import me.lifeoferic.mplay.MPlayFragment;

/**
 * Created by socheong on 4/29/15.
 */
public class LegalFragment extends MPlayFragment {

	protected static final String TITLE = "Legal";

	public static LegalFragment newInstance() {
		return new LegalFragment();
	}

	@Override
	public void onResume() {
		super.onResume();
		getMainActivity().setTitle(TITLE);
	}
}
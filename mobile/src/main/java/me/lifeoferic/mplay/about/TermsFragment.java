package me.lifeoferic.mplay.about;

import me.lifeoferic.mplay.MPlayFragment;

/**
 * Created by socheong on 4/29/15.
 */
public class TermsFragment extends MPlayFragment {

	protected static final String TITLE = "Terms & Agreements";

	public static TermsFragment newInstance() {
		return new TermsFragment();
	}

	@Override
	public void onResume() {
		super.onResume();
		getMainActivity().setTitle(TITLE);
	}
}

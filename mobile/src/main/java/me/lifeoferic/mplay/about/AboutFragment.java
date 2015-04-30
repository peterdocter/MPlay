package me.lifeoferic.mplay.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.Switchboard;

/**
 * Created by socheong on 4/25/15.
 */
public class AboutFragment extends MPlayFragment {

	protected static final String TITLE = "About";

	private TextView mVersionNumberView;
	private Button mAboutDeveloperButton;
	private Button mLegalButton;
	private Button mTermsButton;

	public static AboutFragment newInstance() {
		return new AboutFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		mVersionNumberView = (TextView) rootView.findViewById(R.id.version_number);
		mAboutDeveloperButton = (Button) rootView.findViewById(R.id.about_developer_button);
		mLegalButton = (Button) rootView.findViewById(R.id.about_legal_button);
		mTermsButton = (Button) rootView.findViewById(R.id.about_terms_button);

		setListeners();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mVersionNumberView.setText(Switchboard.getInstance().getVersion());
	}

	@Override
	public void onResume() {
		super.onResume();
		getMainActivity().getSupportActionBar().setTitle(TITLE);
	}

	private void setListeners() {
		mAboutDeveloperButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMainActivity().openFragment(AboutDeveloperFragment.newInstance());
			}
		});
		mLegalButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMainActivity().openFragment(LegalFragment.newInstance());
			}
		});
		mTermsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getMainActivity().openFragment(TermsFragment.newInstance());
			}
		});
	}
}

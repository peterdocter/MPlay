package me.lifeoferic.mplay.musicplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.musicplayer.MusicPlayerFragment;

/**
 * Created by socheong on 4/11/15.
 */
public class MPlayerView extends LinearLayout {

	private ImageButton mBackButton;
	private ImageButton mPlayButton;
	private ImageButton mNextButton;
	private ImageButton mForwardButton;
	private ImageButton mRewindButton;
	private TextView mTitleView;
	private TextView mDurationView;
	private TextView mCurrentTimeView;
	private SeekBar mSeekBar;
	private MusicPlayerFragment.MusicFragmentListener mFragmentListener;

	public MPlayerView(Context context) {
		super(context);
		initialize();
	}

	public MPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public MPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}

	private void initialize() {
		setupViews();
		setupListeners();
	}

	public void setMusicFragmentListener(MusicPlayerFragment.MusicFragmentListener listener) {
		mFragmentListener = listener;
	}

	private void setupViews() {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_player, this, true);
		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mPlayButton = (ImageButton) findViewById(R.id.play_button);
		mNextButton = (ImageButton) findViewById(R.id.next_button);
		mRewindButton = (ImageButton) findViewById(R.id.rewind_button);
		mForwardButton = (ImageButton) findViewById(R.id.forward_button);
		mTitleView = (TextView) findViewById(R.id.title);
		mDurationView = (TextView) findViewById(R.id.duration);
		mCurrentTimeView = (TextView) findViewById(R.id.current_time);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
	}

	private void setupListeners() {
		mNextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentListener.next();
			}
		});
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mFragmentListener.previous();
			}
		});

		mPlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.isSelected()) {
					view.setSelected(false);
					mFragmentListener.play();
				} else {
					view.setSelected(true);
					mFragmentListener.pause();
				}
			}
		});
		mForwardButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFragmentListener.forward();
			}
		});
		mRewindButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mFragmentListener.rewind();
			}
		});
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	public void updateSeekbar(long current, long total) {
		int progress = (int) (100 * current / total);
		mSeekBar.setProgress(progress);
	}
}

package me.lifeoferic.mplay.musicplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import me.lifeoferic.mplay.MainActivity;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.Utils;
import me.lifeoferic.mplay.models.Music;

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
	private TextView mArtistView;
	private TextView mDurationView;
	private SeekBar mSeekBar;

	private MainActivity.MusicActivityListener mFragmentListener;

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

	public void setMusicFragmentListener(MainActivity.MusicActivityListener listener) {
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
		mArtistView = (TextView) findViewById(R.id.artist);
		mDurationView = (TextView) findViewById(R.id.duration);
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
				view.setSelected(!view.isSelected());
				if (view.isSelected()) {
					mPlayButton.setImageResource(R.drawable.ic_action_pause_circle_outline);
					mFragmentListener.play();
				} else {
					mPlayButton.setImageResource(R.drawable.ic_action_play_circle_outline);
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
	}

	public void setMusicInfo(Music music) {

		mTitleView.setText(music.getTitle());
		mArtistView.setText(music.getArtist());
		mDurationView.setText(Utils.getFormattedTimeString(music.getLength()));
	}

	public void handlePlay() {
		mPlayButton.setSelected(true);
		mPlayButton.setImageResource(R.drawable.ic_action_pause_circle_outline);
	}

	public void handlePause() {
		mPlayButton.setSelected(false);
		mPlayButton.setImageResource(R.drawable.ic_action_play_circle_outline);
	}
}

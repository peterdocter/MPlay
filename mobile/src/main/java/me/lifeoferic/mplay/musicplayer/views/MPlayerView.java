package me.lifeoferic.mplay.musicplayer.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;

import me.lifeoferic.mplay.MainActivity;
import me.lifeoferic.mplay.PlayerTimer;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.Utils;
import me.lifeoferic.mplay.models.Music;

/**
 * Created by socheong on 4/11/15.
 */
public class MPlayerView extends LinearLayout {

	private static final String TAG = MPlayerView.class.getSimpleName();

	private ImageButton mBackButton;
	private ImageButton mPlayButton;
	private ImageButton mNextButton;
	private ImageButton mForwardButton;
	private ImageButton mRewindButton;
	private TextView mTitleView;
	private TextView mArtistView;
	private TextView mDurationView;
	private TextView mCurrentTimeView;
	private SeekBar mSeekBar;
	private Timer mPlayTimer;
	private Context mContext;

	private MainActivity.MusicActivityListener mFragmentListener;

	public MPlayerView(Context context) {
		super(context);
		initialize(context);
	}

	public MPlayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public MPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context);
	}

	private void initialize(Context context) {
		mContext = context;
		mPlayTimer = new Timer();
		setupViews();
		setupSeekbar();
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

	private void setupSeekbar() {
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				//if (fromUser) {
				//seekTo(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}

	public void setMusicInfo(final Music music) {
		mTitleView.setText(music.getTitle());
		mArtistView.setText(music.getArtist());
		mDurationView.setText(Utils.getFormattedTimeString(music.getLength()));
		new Thread(new Runnable() {
			@Override
			public void run() {
				long currentPosition = 0;
				boolean musicThreadFinished = false;
				while (!musicThreadFinished) {
					try {
						Thread.sleep(1000);
						Log.d(TAG, "Thread sleeping");
						currentPosition = PlayerTimer.getInstance().getCurrentTime();
					} catch (Exception e) {
						return;
					}
					final int total = (int) music.getLength() / 1000;
					final String currentTime = Utils.getFormattedTimeString(currentPosition);

					mSeekBar.setMax(total); //song duration
					mSeekBar.setProgress((int) currentPosition / 1000);  //for current song progress
					((Activity) mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mCurrentTimeView.setText(currentTime);
						}
					});

//					mSeekBar.setSecondaryProgress(getBufferPercentage());   // for buffer progress
					if (currentPosition == total) {
						musicThreadFinished = true;
					}
				}

			Log.d(TAG, "Music thread finished");
			}
		}).start();
	}

	public void handlePlay() {
		mPlayButton.setSelected(true);
		mPlayButton.setImageResource(R.drawable.ic_action_pause_circle_outline);
	}
}

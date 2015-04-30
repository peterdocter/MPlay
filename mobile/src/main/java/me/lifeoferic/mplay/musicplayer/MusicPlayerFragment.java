package me.lifeoferic.mplay.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.models.Music;
import me.lifeoferic.mplay.musicplayer.views.MPlayerView;

/**
 * Music Player Fragment
 */
public class MusicPlayerFragment extends MPlayFragment {

	private static final String TAG = MusicPlayerFragment.class.getSimpleName();

	private ArrayList<Music> mMusicList;
	private boolean paused = false;
	private boolean playbackPaused = false;
	private MusicService musicService;
	private boolean musicBound = false;
	private Intent playIntent;
	private MediaController.MediaPlayerControl mController;
	Handler seekHandler = new Handler();

	private ListView mMusicListView;
	private TextView mTitleView;
	private TextView mDurationView;
	private TextView mCurrentTimeView;
	private MPlayerView mMusicPlayerView;

	public static MusicPlayerFragment newInstance() {
		return new MusicPlayerFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mp, container, false);
		mMusicListView = (ListView) view.findViewById(R.id.music_listview);
		mMusicPlayerView = (MPlayerView) view.findViewById(R.id.music_player_view);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart()");
		mMusicList = new ArrayList<>();
		getSongList();
		sort();

		mMusicListView.setAdapter(new MusicAdapter(getActivity(), mMusicList));
		mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				songPicked(position);
			}
		});
//		mSeekBar.setProgress(0);
//		mSeekBar.setMax(100);
//		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//			@Override
//			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//				System.out.println("Progress: " + progress);
//				if (fromUser) {
//					mController.seekTo(progress);
//				}
//				mSeekBar.setProgress(progress);
//			}
//
//			@Override
//			public void onStartTrackingTouch(SeekBar seekBar) {
//
//			}
//
//			@Override
//			public void onStopTrackingTouch(SeekBar seekBar) {
//				int seekValue = seekBar.getProgress();
//			}
//		});
		setupController();
		if (playIntent == null) {
			playIntent = new Intent(getActivity(), MusicService.class);
			getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
			getActivity().startService(playIntent);
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		if (paused) {
			setupController();
			paused = false;
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		paused = true;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		getActivity().stopService(playIntent);
		getActivity().unbindService(musicConnection);
		musicService = null;
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_shuffle:
				Toast.makeText(getActivity(), "SHUFFLE", Toast.LENGTH_SHORT).show();
				musicService.toggleShuffle();
				break;
			case R.id.action_end:
				Toast.makeText(getActivity(), "END", Toast.LENGTH_SHORT).show();
				getActivity().stopService(playIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getSongList() {
		ContentResolver musicResolver = getActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		if(musicCursor != null && musicCursor.moveToFirst()){
			//get columns
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ARTIST);
			int lengthColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				long thisLength = musicCursor.getLong(lengthColumn);
				int thisIsMusic = musicCursor.getInt(isMusicColumn);
				if (thisIsMusic == 1) {
					mMusicList.add(new Music(thisId, thisTitle, thisArtist, thisLength));
				}
			}
			while (musicCursor.moveToNext());
		}
	}

	public void sort() {
		Collections.sort(mMusicList, new Comparator<Music>() {
			public int compare(Music a, Music b) {
				return a.getTitle().compareTo(b.getTitle());
			}
		});
	}

	public void update() {
		System.out.println("update: " + mController.getCurrentPosition());
//		mSeekBar.setProgress(mController.getCurrentPosition() / 7000);
		seekHandler.postDelayed(run, 1000);
	}

	public void stopUpdate() {
		seekHandler.removeCallbacks(run);
	}

	Runnable run = new Runnable() {
		@Override public void run() {
			update();
		}
	};

	public interface MusicFragmentListener {
		public void play();
		public void pause();
		public void next();
		public void previous();
		public void rewind();
		public void forward();
	}

	public void setupController() {
		MusicFragmentListener musicFragmentListener = new MusicFragmentListener() {
			@Override
			public void play() {
				mController.start();
			}

			@Override
			public void pause() {
				mController.pause();
			}

			@Override
			public void next() {
				playNext();
			}

			@Override
			public void previous() {
				playPrev();
			}

			@Override
			public void rewind() {
//				rewindTrack();
			}

			@Override
			public void forward() {
//				forwardTrack();
			}
		};
		mMusicPlayerView.setMusicFragmentListener(musicFragmentListener);

		mController = new MediaController.MediaPlayerControl() {

			@Override
			public void start() {
				Log.d(TAG, "start");
				playbackPaused = false;
				musicService.play();
				update();
			}

			@Override
			public void pause() {
				Log.d(TAG, "pause");
				playbackPaused = true;
				musicService.pause();
				stopUpdate();
			}

			@Override
			public int getDuration() {
				if (musicService != null && musicBound && musicService.isPlaying()) {
					return musicService.getDuration();
				} else {
					return 0;
				}
			}

			@Override
			public int getCurrentPosition() {
				if (musicService != null && musicBound && musicService.isPlaying()) {
					return musicService.getPosition();
				} else {
					return 0;
				}
			}

			@Override
			public void seekTo(int i) {
				Log.d(TAG, "seekTo: " + i);
				musicService.seek(i);
			}

			@Override
			public boolean isPlaying() {
				if (musicService != null && musicBound) {
					return musicService.isPlaying();
				}
				return false;
			}

			@Override
			public int getBufferPercentage() {
				return 0;
			}

			@Override
			public boolean canPause() {
				return true;
			}

			@Override
			public boolean canSeekBackward() {
				return true;
			}

			@Override
			public boolean canSeekForward() {
				return true;
			}

			@Override
			public int getAudioSessionId() {
				return 0;
			}
		};
	}

	private void playNext() {
		Log.d(TAG, "playNext");
		musicService.playNext();
		if (playbackPaused) {
			setupController();
			playbackPaused = false;
		}
	}

	private void playPrev() {
		Log.d(TAG, "playPrev");
		musicService.playPrev();
		if (playbackPaused) {
			setupController();
			playbackPaused = false;
		}
	}

	public void songPicked(int position){
		Log.d(TAG, "song picked");
		musicService.setSong(position);
		musicService.playSong();
		if (playbackPaused) {
			setupController();
			playbackPaused = false;
		}
	}

	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
			//get service
			musicService = binder.getService();
			//pass list
			musicService.setList(mMusicList);
			musicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			musicBound = false;
		}
	};
}

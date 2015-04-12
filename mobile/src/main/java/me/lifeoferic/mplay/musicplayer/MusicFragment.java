package me.lifeoferic.mplay.musicplayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.models.Music;
import me.lifeoferic.mplay.widget.MusicController;

/**
 * Music Player Fragment
 */
public class MusicFragment extends MPlayFragment {

	private static final String TAG = MusicFragment.class.getSimpleName();

	private ArrayList<Music> mMusicList;
	private ListView mMusicListView;
	private MusicController controller;
	private boolean paused = false;
	private boolean playbackPaused = false;
	private MusicService musicService;
	private boolean musicBound = false;
	private Intent playIntent;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mp, container, false);
		mMusicListView = (ListView) view.findViewById(R.id.music_listview);
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
		setController();
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
			setController();
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
		controller.hide();
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
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				mMusicList.add(new Music(thisId, thisTitle, thisArtist));
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

	public void setController() {
		if (controller == null) {
			System.out.println("controller set");
			controller = new MusicPlayerController(getActivity());
			View.OnClickListener playNextListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playNext();
				}
			};
			View.OnClickListener playPrevListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					playPrev();
				}
			};
			controller.setPrevNextListeners(playNextListener, playPrevListener);
			MediaController.MediaPlayerControl control = new MediaController.MediaPlayerControl() {

				@Override
				public void start() {
					Log.d(TAG, "start");
					playbackPaused = false;
					musicService.play();
				}

				@Override
				public void pause() {
					Log.d(TAG, "pause");
					playbackPaused = true;
					musicService.pause();
				}

				@Override
				public int getDuration() {
					if (musicService != null && musicBound && musicService.isPlaying())
						return musicService.getDuration();
					else
						return 0;
				}

				@Override
				public int getCurrentPosition() {
					if (musicService != null && musicBound && musicService.isPlaying())
						return musicService.getPosition();
					else
						return 0;
				}

				@Override
				public void seekTo(int i) {
					Log.d(TAG, "seekTo");
					musicService.seek(i);
				}

				@Override
				public boolean isPlaying() {
					if (musicService != null && musicBound)
						return musicService.isPlaying();
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
			controller.setMediaPlayer(control);
			controller.setAnchorView(mMusicListView);
			controller.setEnabled(true);
		}
	}

	private void playNext() {
		Log.d(TAG, "playNext");
		musicService.playNext();
		if (playbackPaused) {
			setController();
			playbackPaused=false;
		}
		controller.show(0);
	}

	private void playPrev() {
		Log.d(TAG, "playPrev");
		musicService.playPrev();
		if (playbackPaused) {
			setController();
			playbackPaused=false;
		}
		controller.show(0);
	}

	public void songPicked(int position){
		Log.d(TAG, "song picked");
		musicService.setSong(position);
		musicService.playSong();
		if (playbackPaused) {
			setController();
			playbackPaused = false;
		}
		controller.show(0);
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

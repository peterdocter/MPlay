package me.lifeoferic.mplay;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import java.util.ArrayList;

import me.lifeoferic.mplay.models.Music;
import me.lifeoferic.mplay.musiclist.LibraryFragment;
import me.lifeoferic.mplay.musicplayer.MusicService;
import me.lifeoferic.mplay.musicplayer.views.MPlayerView;


public class MainActivity extends ActionBarActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String CURRENT_FRAGMENT = "current_fragment";

	private DrawerLayout mDrawerLayout;
	private Toolbar mToolbar;
	private ActionBarDrawerToggle mDrawerToggle;
	private MPlayerView mMusicPlayerView;

	private ArrayList<Music> mMusicList;
	private boolean isPaused = false;
	private boolean isPlaybackPaused = false;
	private MusicService mMusicService;
	private boolean isMusicBound = false;
	private Intent mPlayIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mMusicPlayerView = (MPlayerView) findViewById(R.id.music_player_view);

		mMusicList = new ArrayList<>();
	}


	@Override
	protected void onStart() {
		super.onStart();
		setupActionToolBar();
		setupDrawerLayout();
		playerOnStart();
		openFragment(LibraryFragment.newInstance());
	}

	@Override
	protected void onResume() {
		super.onResume();
		playerOnResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		playerOnPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		playerOnDestroy();
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(GravityCompat.START);
		menu.findItem(R.id.action_shuffle).setVisible(!drawerOpen);
		menu.findItem(R.id.action_end).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
			case R.id.action_shuffle:
				Toast.makeText(this, "SHUFFLE", Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_end:
				Toast.makeText(this, "END", Toast.LENGTH_SHORT).show();
				stopService(mPlayIntent);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
			mDrawerLayout.closeDrawers();
			return;
		}
		super.onBackPressed();
	}

	public void setupActionToolBar() {
		setSupportActionBar(mToolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	public void setupDrawerLayout() {
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {

			@Override
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/* Music player components */

	private void playerOnStart() {
		mMusicPlayerView.setMusicFragmentListener(musicFragmentListener);
		if (mPlayIntent == null) {
			mPlayIntent = new Intent(this, MusicService.class);
			bindService(mPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
			startService(mPlayIntent);
		}
	}

	private void playerOnResume() {
		isPaused = false;
	}

	private void playerOnPause() {
		isPaused = true;
	}

	private void playerOnDestroy() {
		stopService(mPlayIntent);
		unbindService(musicConnection);
		mMusicService = null;
	}

	public void songPicked(ArrayList<Music> musicList){
		Log.d(TAG, "song picked");
		mMusicList = musicList;
		mMusicService.setList(mMusicList);
		mMusicService.setSong(0);
		mMusicService.playSong();
		if (isPlaybackPaused) {
			isPlaybackPaused = false;
		}
	}

	public void openFragment(Fragment fragment) {
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.main_fragment_container, fragment, CURRENT_FRAGMENT).commit();
		mDrawerLayout.closeDrawer(GravityCompat.START);
	}

	private MediaController.MediaPlayerControl mController = new MediaController.MediaPlayerControl() {

		@Override
		public void start() {
			Log.d(TAG, "start");
			isPlaybackPaused = false;
			mMusicService.play();
		}

		@Override
		public void pause() {
			Log.d(TAG, "pause");
			isPlaybackPaused = true;
			mMusicService.pause();
		}

		@Override
		public int getDuration() {
			if (mMusicService != null && isMusicBound && mMusicService.isPlaying()) {
				return mMusicService.getDuration();
			} else {
				return 0;
			}
		}

		@Override
		public int getCurrentPosition() {
			if (mMusicService != null && isMusicBound && mMusicService.isPlaying()) {
				return mMusicService.getPosition();
			} else {
				return 0;
			}
		}

		@Override
		public void seekTo(int i) {
			Log.d(TAG, "seekTo: " + i);
			mMusicService.seek(i);
		}

		@Override
		public boolean isPlaying() {
			if (mMusicService != null && isMusicBound) {
				return mMusicService.isPlaying();
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

	private MusicFragmentListener musicFragmentListener = new MusicFragmentListener() {
		@Override
		public void play() {
			if (mMusicList.size() > 0) {
				mController.start();
			}
		}

		@Override
		public void pause() {
			if (mMusicList.size() > 0) {
				mController.pause();
			}
		}

		@Override
		public void next() {
			if (mMusicList.size() > 0) {
				mMusicService.playNext();
				if (isPlaybackPaused) {
					isPlaybackPaused = false;
				}
			}
		}

		@Override
		public void previous() {
			if (mMusicList.size() > 0) {
				mMusicService.playPrev();
				if (isPlaybackPaused) {
					isPlaybackPaused = false;
				}
			}
		}

		@Override
		public void rewind() {
			//				rewindTrack();
		}

		@Override
		public void forward() {
			//				forwardTrack();
		}

		@Override
		public void seekTo(int progress) {
			mController.seekTo(progress);
		}
	};

	private ServiceConnection musicConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
			mMusicService = binder.getService();
			mMusicService.setList(mMusicList);
			isMusicBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isMusicBound = false;
		}
	};

	public interface MusicFragmentListener {
		void play();
		void pause();
		void next();
		void previous();
		void rewind();
		void forward();
		void seekTo(int progress);
	}
}

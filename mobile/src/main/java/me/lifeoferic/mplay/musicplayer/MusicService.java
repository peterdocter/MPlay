package me.lifeoferic.mplay.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import me.lifeoferic.mplay.MainActivity;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.models.Music;

/**
 * Created by socheong on 4/11/15.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
		MediaPlayer.OnCompletionListener {

	private static final String TAG = MusicService.class.getSimpleName();

	private String songTitle = "";
	private static final int NOTIFY_ID = 1;

	private MediaPlayer player;
	private ArrayList<Music> musicList;
	private int currentSongPosition;
	private final IBinder musicBind = new MusicBinder();

	@Override
	public void onCreate() {
		super.onCreate();
		currentSongPosition = 0;
		initMusicPlayer();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return musicBind;
	}

	@Override
	public boolean onUnbind(Intent intent){
		player.stop();
		player.release();
		return false;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d(TAG, "onPrepared");
		mp.start();

		Intent notIntent = new Intent(this, MainActivity.class);
		notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendInt = PendingIntent.getActivity(this, 0,
				notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification.Builder builder = new Notification.Builder(this);

		builder.setContentIntent(pendInt)
				.setSmallIcon(R.drawable.ic_action_android)
				.setTicker(songTitle)
				.setOngoing(true)
				.setContentTitle("Playing")
				.setContentText(songTitle);
		Notification not = builder.build();

		startForeground(NOTIFY_ID, not);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.d(TAG, "onError");
		mp.reset();
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.d(TAG, "onCompletion");
			releaseMediaPlayer();
			initMusicPlayer();
			playNext();
	}

	@Override
	public void onDestroy() {
		releaseMediaPlayer();
		stopForeground(true);
	}

	private void releaseMediaPlayer() {
		try {
			if (player != null) {
				if (player.isPlaying()) {
					player.stop();
				}
				player.release();
				player = null;
			}
		} catch (Exception e) {

		}
	}

	public void initMusicPlayer() {
		Log.d(TAG, "initialize");
		player = new MediaPlayer();
		player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnPreparedListener(this);
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
	}

	public void setList(ArrayList<Music> songs) {
		musicList = songs;
	}

	public void setSong(int songIndex){
		currentSongPosition = songIndex;
	}

	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	public void playSong() {
		Log.d(TAG, "playSong: " + currentSongPosition);

		try {
			player.reset();
			Music playSong = musicList.get(currentSongPosition);
			Log.d(TAG, "songLength: " + playSong.getLength());
			songTitle = playSong.getTitle();
			long currentSong = playSong.getID();
			Uri trackUri = ContentUris.withAppendedId(
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					currentSong);
			player.setDataSource(getApplicationContext(), trackUri);
			player.prepare();
			player.start();
			// Displaying Song title
			songTitle = playSong.getTitle();
//			songTitleLabel.setText(songTitle);

			// Changing Button Image to pause image
//			btnPlay.setImageResource(R.drawable.btn_pause);

			// set Progress bar values
//			songProgressBar.setProgress(0);
//			songProgressBar.setMax(100);

			// Updating progress bar
//			updateProgressBar();
		} catch (IllegalArgumentException|IllegalStateException|IOException e) {
			e.printStackTrace();
		}
	}

	public void playPrev() {
		Log.d(TAG, "playPrev");
		if (currentSongPosition > 0) {
			currentSongPosition = currentSongPosition - 1;
		} else {
			// play last song
			currentSongPosition = musicList.size() - 1;
		}
		playSong();
	}

	public void playNext() {
		Log.d(TAG, "playNext");

		if (currentSongPosition < (musicList.size() - 1)) {
			currentSongPosition = currentSongPosition + 1;
		} else {
			// play first song
			currentSongPosition = 0;
		}
		playSong();
	}

	public int getPosition(){
		return player.getCurrentPosition();
	}

	public int getDuration(){
		return player.getDuration();
	}

	public boolean isPlaying(){
		return player.isPlaying();
	}

	public void pause(){
		player.pause();
	}

	public void seek(int position){
		player.seekTo(position);
	}

	public void play(){
		player.start();
	}
}

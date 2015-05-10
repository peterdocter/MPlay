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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import me.lifeoferic.mplay.MainActivity;
import me.lifeoferic.mplay.MusicPlayerManager;
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
	private final IBinder musicBind = new MusicBinder();
	private Music mCurrentMusic;

	@Override
	public void onCreate() {
		super.onCreate();
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
		Intent intent = new Intent(MusicPlayerManager.PLAY_NEXT);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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

	public void playSong(Music music) {
		Log.d(TAG, "playSong: " + music.getTitle());
		mCurrentMusic = music;
		try {
			player.reset();
			songTitle = mCurrentMusic.getTitle();
			long currentSong = mCurrentMusic.getID();
			Uri trackUri = ContentUris.withAppendedId(
					android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					currentSong);
			player.setDataSource(getApplicationContext(), trackUri);
			player.prepare();
			player.start();
			songTitle = mCurrentMusic.getTitle();

		} catch (IllegalArgumentException|IllegalStateException|IOException e) {
			e.printStackTrace();
		}
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

	public void play() {
		player.start();
	}

	public class MusicBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}
}

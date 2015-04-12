package me.lifeoferic.mplay.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import me.lifeoferic.mplay.widget.MusicController;

/**
 * Created by socheong on 4/11/15.
 */
public class MusicPlayerController extends MusicController {

	private static final String TAG = MusicPlayerController.class.getSimpleName();

	public MusicPlayerController(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "onBackPressed");
			super.hide();
			Context c = getContext();
			((Activity) c).finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}

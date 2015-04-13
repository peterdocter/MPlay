package me.lifeoferic.mplay.musicplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import me.lifeoferic.mplay.R;

/**
 * Created by socheong on 4/11/15.
 */
public class MPlayerView extends LinearLayout {
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
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_player, this, true);
	}
}

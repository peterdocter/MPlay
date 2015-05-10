package me.lifeoferic.mplay;

import java.util.Timer;

/**
 * Created by socheong on 5/9/15.
 */
public class PlayerTimer {

	private static PlayerTimer instance;
	private long lastPausedTime;
	private long tempTotalTime;

	public PlayerTimer() {
		tempTotalTime = 0;
		lastPausedTime = 0;
	}

	public static PlayerTimer getInstance() {
		if (instance == null) {
			instance = new PlayerTimer();
		}

		return instance;
	}

	public void start() {
		lastPausedTime = System.currentTimeMillis();
	}

	public void pause() {
		tempTotalTime += System.currentTimeMillis() - lastPausedTime;
	}

	public void reset() {
		tempTotalTime = 0;
		lastPausedTime = 0;
	}

	public long getCurrentTime() {
		return tempTotalTime + System.currentTimeMillis() - lastPausedTime;
	}
}

package me.lifeoferic.mplay;

import android.app.Application;

/**
 * Created by socheong on 4/29/15.
 */
public class MPlayApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Switchboard.getInstance().init(getApplicationContext());
	}
}

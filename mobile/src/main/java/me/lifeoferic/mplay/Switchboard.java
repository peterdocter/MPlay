package me.lifeoferic.mplay;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by socheong on 4/29/15.
 */
public class Switchboard {

	public static Switchboard instance;
	public Context mContext;
	public DevelopmentStage mDevStage = DevelopmentStage.ALPHA;

//	public static final boolean DEBUGGING = false;

	public static Switchboard getInstance() {
		if (instance == null) {
			instance = new Switchboard();
		}
		return instance;
	}

	public void init(Context context) {
		mContext = context;
	}

	private Switchboard() {
	}

	public enum EnvironmentMode {
		DEVELOPMENT, PRE_RELEASE, PRODUCTION,
	}

	public enum DevelopmentStage {
		ALPHA, BETA, STABLE
	}

	public String getVersion() {
		String versionNumber;
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			versionNumber = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			versionNumber = "0.00";
		}

		return versionNumber + mDevStage.toString();
	}
}

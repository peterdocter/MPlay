package me.lifeoferic.mplay;

/**
 * Created by socheong on 5/9/15.
 */
public class Utils {

	public static String getFormattedTimeString(long milisecond) {
		long secondRawValue = milisecond / 1000;
		long minute = secondRawValue / 60;
		long second = secondRawValue % 60;
		return String.valueOf(minute) + ":" + String.format("%2d", second);
	}
}

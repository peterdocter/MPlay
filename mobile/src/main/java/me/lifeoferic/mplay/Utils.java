package me.lifeoferic.mplay;

/**
 * Created by socheong on 5/9/15.
 */
public class Utils {

	public static String getFormattedTimeString(long milisecond) {
		System.out.println("raw mill: " + milisecond);
		long secondRawValue = milisecond / 1000;
		System.out.println("raw second: " + secondRawValue);
		long minute = secondRawValue / 60;
		System.out.println("minute: " + minute);
		long second = secondRawValue % 60;
		System.out.println("second: " + second);
		return String.valueOf(minute) + ":" + String.valueOf(second);
	}
}

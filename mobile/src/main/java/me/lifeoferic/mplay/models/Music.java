package me.lifeoferic.mplay.models;

/**
 * Created by socheong on 4/11/15.
 */
public class Music {
	private long id;
	private String title;
	private String artist;
	private long length;

	public Music(long songID, String songTitle, String songArtist, long songLength) {
		id = songID;
		title = songTitle;
		artist = songArtist;
		length = songLength;
	}

	public long getID() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public long getLength() {
		return length;
	}
}

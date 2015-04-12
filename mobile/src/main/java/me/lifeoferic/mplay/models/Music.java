package me.lifeoferic.mplay.models;

/**
 * Created by socheong on 4/11/15.
 */
public class Music {
	private long id;
	private String title;
	private String artist;

	public Music(long songID, String songTitle, String songArtist) {
		id = songID;
		title = songTitle;
		artist = songArtist;
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
}

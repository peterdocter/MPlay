package me.lifeoferic.mplay.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import me.lifeoferic.mplay.MPlayFragment;
import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.models.Music;

/**
 * Music Player Fragment
 */
public class MusicPlayerFragment extends MPlayFragment {

	private static final String TAG = MusicPlayerFragment.class.getSimpleName();

	private ListView mMusicListView;
	private ArrayList<Music> mMusicList;

	public static MusicPlayerFragment newInstance() {
		return new MusicPlayerFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mp, container, false);
		mMusicListView = (ListView) view.findViewById(R.id.music_listview);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		setupListview();
	}

	private void setupListview() {
		mMusicList = new ArrayList<>();
		mMusicListView.setAdapter(new MusicAdapter(getMainActivity(), mMusicList));
		mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				ArrayList<Music> musicList = new ArrayList<>();
				musicList.addAll(mMusicList.subList(position, mMusicList.size()));
				musicList.addAll(mMusicList.subList(0, position));
				getMainActivity().songPicked(musicList);
			}
		});
		getSongList();
		sortMusic();
	}

	public void getSongList() {
		ContentResolver musicResolver = getMainActivity().getContentResolver();
		Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
		if(musicCursor != null && musicCursor.moveToFirst()){
			//get columns
			int titleColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.TITLE);
			int idColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media._ID);
			int artistColumn = musicCursor.getColumnIndex
					(android.provider.MediaStore.Audio.Media.ARTIST);
			int lengthColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
			int isMusicColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC);
			//add songs to list
			do {
				long thisId = musicCursor.getLong(idColumn);
				String thisTitle = musicCursor.getString(titleColumn);
				String thisArtist = musicCursor.getString(artistColumn);
				long thisLength = musicCursor.getLong(lengthColumn);
				int thisIsMusic = musicCursor.getInt(isMusicColumn);
				if (thisIsMusic == 1) {
					mMusicList.add(new Music(thisId, thisTitle, thisArtist, thisLength));
				}
			}
			while (musicCursor.moveToNext());
		}
	}

	public void sortMusic() {
		Collections.sort(mMusicList, new Comparator<Music>() {
			public int compare(Music a, Music b) {
				return a.getTitle().compareTo(b.getTitle());
			}
		});
	}
}

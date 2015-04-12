package me.lifeoferic.mplay.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import me.lifeoferic.mplay.R;
import me.lifeoferic.mplay.models.Music;

/**
 * Music List adapter
 */
public class MusicAdapter extends BaseAdapter {

	private ArrayList<Music> mMusicList;
	private LayoutInflater mInflater;

	public MusicAdapter(Context c, ArrayList<Music> songs){
		mMusicList = songs;
		mInflater = LayoutInflater.from(c);
	}

		@Override
		public int getCount() {
			return mMusicList.size();
		}

		@Override
		public Music getItem(int position) {
			return mMusicList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {
			final MusicViewHolder holder;
			if (convertView == null) {
				holder = new MusicViewHolder();
				convertView = mInflater.inflate(R.layout.music_list_item, viewGroup, false);
				holder.titleView = (TextView) convertView.findViewById(R.id.song_title);
				holder.artistView = (TextView) convertView.findViewById(R.id.song_artist);
				convertView.setTag(holder);
			} else {
				holder = (MusicViewHolder) convertView.getTag();
			}

			Music music = getItem(position);
			if (music.getTitle() != null) {
				holder.titleView.setText(music.getTitle());
			}
			if (music.getArtist() != null) {
				holder.artistView.setText(music.getArtist());
			}
			return convertView;
		}
	}

class MusicViewHolder {
	TextView titleView;
	TextView artistView;
}

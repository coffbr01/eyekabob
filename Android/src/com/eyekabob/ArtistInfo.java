package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.eyekabob.models.Artist;

public class ArtistInfo extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_info);
		Artist artist = (Artist)getIntent().getExtras().get("artist");
		Log.d(getClass().getName(), "Artist name is: " + artist.getName());

		// TODO: request info based on artist name
	}

}

package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class ArtistInfo extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_info);
		String artist = getIntent().getExtras().getString("artist");
		Log.d(getClass().getName(), "Artist name is: " + artist);

		// TODO: request info based on artist name
	}

}

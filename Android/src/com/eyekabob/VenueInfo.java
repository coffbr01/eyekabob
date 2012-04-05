package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class VenueInfo extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.venue_info);
		String artist = getIntent().getExtras().getString("venue");
		Log.d(getClass().getName(), "Venue name is: " + artist);

		// TODO: request info based on venue name
	}

}

/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.eyekabob.models.Venue;

public class VenueInfo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_info);
        Venue venue = (Venue)getIntent().getExtras().get("venue");
        Log.d(getClass().getName(), "Venue name is: " + venue.getName());

        // TODO: request info based on venue name
    }

}

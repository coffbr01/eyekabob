/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Home extends EyekabobActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
    }

    public void findMusicHandler(View v) {
        Intent findMusicIntent = new Intent(this, FindMusic.class);
        startActivity(findMusicIntent);
    }

    public void homeGalleryHandler(View v) {
        Intent prototypeIntent = new Intent(this, Prototype.class);
        prototypeIntent.putExtra("resourceId", R.drawable.prototype_gallery);
        startActivity(prototypeIntent);
    }

    public void homeStatusHandler(View v) {
        Intent prototypeIntent = new Intent(this, Prototype.class);
        prototypeIntent.putExtra("resourceId", R.drawable.prototype_status);
        startActivity(prototypeIntent);
    }

    public void homeCheckinHandler(View v) {
        Intent prototypeIntent = new Intent(this, Prototype.class);
        prototypeIntent.putExtra("resourceId", R.drawable.prototype_checkin);
        startActivity(prototypeIntent);
        // TODO this was commented out. the prototype replaced it
        //EyekabobHelper.Foursquare.searchNearby(this);
    }

    public void homeSettingsHandler(View v) {
        Intent prototypeIntent = new Intent(this, Prototype.class);
        prototypeIntent.putExtra("resourceId", R.drawable.prototype_settings);
        startActivity(prototypeIntent);
    }

    public void homeBandHandler(View v) {
        Intent signInIntent = new Intent(this, SignInView.class);
        startActivity(signInIntent);
    }

    public void homeCalendarHandler(View v) {
        Intent calendarIntent = new Intent(Intent.ACTION_VIEW);
        calendarIntent.setType("vnd.android.cursor.item/event");
        startActivity(calendarIntent);
    }
}

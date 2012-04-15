/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eyekabob.util.EyekabobHelper;

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

    public void homePicsHandler(View v) {
    }

    public void homeFriendsHandler(View v) {
    }

    public void homeCheckinHandler(View v) {
    	EyekabobHelper.Foursquare.authenticate(this, CheckinSearchList.class);
    }

    public void homeSettingsHandler(View v) {
    }

    public void homeBandHandler(View v) {
    }

    public void homeForumHandler(View v) {
    }

    public void homeAdHandler(View v) {
    }
}

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.facebook.DialogError;
import com.eyekabob.util.facebook.Facebook;
import com.eyekabob.util.facebook.FacebookError;

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
    	Facebook facebook = EyekabobHelper.Facebook.getInstance();
    	if (!facebook.isSessionValid()) {
    		facebook.authorize(this, new Facebook.DialogListener(){
				public void onComplete(Bundle values) {
					Home.this.startCheckin();
				}
				public void onFacebookError(FacebookError e) {
					Log.e(Home.class.toString(), "Facebook error while refreshing session", e);
				}
				public void onError(DialogError e) {
					Log.e(Home.class.toString(), "Generic error while refreshing Facebook session", e);
				}
				public void onCancel() {
					Log.d(Home.class.toString(), "Canceled Facebook login");
				}
    		});
    		return;
    	}

    	startCheckin();
    }

    private void startCheckin() {
    	Location location = EyekabobHelper.getLocation(this);

    	if (location == null) {
    		Toast.makeText(this, "Could not check in. Make sure GPS is turned on.", Toast.LENGTH_SHORT).show();
    		return;
    	}

    	Intent checkinSearchIntent = new Intent(this, CheckinSearchList.class);
    	checkinSearchIntent.putExtra("location", location);
    	startActivity(checkinSearchIntent);
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

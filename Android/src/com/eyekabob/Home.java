/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.facebook.DialogError;
import com.eyekabob.util.facebook.Facebook;
import com.eyekabob.util.facebook.FacebookError;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
					// TODO: checkin!
				    Toast.makeText(getApplicationContext(), "Facebook login successful", Toast.LENGTH_SHORT).show();
				}
				public void onFacebookError(FacebookError e) {
					// TODO Auto-generated method stub
				}
				public void onError(DialogError e) {
					// TODO Auto-generated method stub
				}
				public void onCancel() {
					// TODO Auto-generated method stub
				}
    		});
    		return;
    	}

    	// TODO: checkin!
    	Toast.makeText(this, "You're already signed in!", Toast.LENGTH_SHORT).show();
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

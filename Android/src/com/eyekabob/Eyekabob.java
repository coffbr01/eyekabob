package com.eyekabob;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

public class Eyekabob extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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

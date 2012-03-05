package com.eyekabob;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Eyekabob extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homeactivity);
    }

    public void findMusicHandler(View v) {
        Intent findMusicIntent = new Intent(this, FindMusic.class);
        startActivity(findMusicIntent);
    }
    public void homeAudioHandler(View v) {
    }
}

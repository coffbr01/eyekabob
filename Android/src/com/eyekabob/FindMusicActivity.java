package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class FindMusicActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmusicactivity);
    }

    public void findByArtistHandler(View v) {
    	Toast.makeText(v.getContext(), "pressed find by artist", 3000);
    }
}

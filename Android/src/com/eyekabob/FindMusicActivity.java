package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class FindMusicActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmusicactivity);
    }

    public void findByArtistHandler(View v) {
        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        String artist = findByArtist.getText().toString();
        Map<String, String> params = new HashMap<String, String>();
        params.put("artist", artist);
        EyekabobHelper.LastFM.makeRequest(getApplicationContext(), "artist.getEvents", params);
    }

}

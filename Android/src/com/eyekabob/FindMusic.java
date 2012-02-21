package com.eyekabob;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FindMusic extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmusicactivity);

        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        findByArtist.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((ImageButton)findViewById(R.id.findByArtistButton)).performClick();
                }
                return false;
            }
        });
    }

    public void findByArtistHandler(View v) {
        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        String artist = findByArtist.getText().toString();
        if ("".equals(artist)) {
        	return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("artist", URLEncoder.encode(artist));
        Uri uri = EyekabobHelper.LastFM.getUri("artist.search", params);
        Intent intent = new Intent(v.getContext(), ArtistResults.class);
        intent.setData(uri);
        startActivity(intent);
    }
    public void findByVenueHandler(View v) {
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        String venue = findByVenue.getText().toString();
        if ("".equals(venue)) {
        	return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("venue", URLEncoder.encode(venue));
        Uri uri = EyekabobHelper.LastFM.getUri("venue.search", params);
        Intent intent = new Intent(v.getContext(), VenueResults.class);
        intent.setData(uri);
        startActivity(intent);
    }
}

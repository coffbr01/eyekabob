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
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        findByVenue.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ((ImageButton)findViewById(R.id.findByVenueButton)).performClick();
                }
                return false;
            }
        });
    }

    public void findByArtistHandler(View v) {
        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        find("artist.search", ArtistResults.class, "artist", findByArtist.getText().toString());
    }

    public void findByVenueHandler(View v) {
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        find("venue.search", VenueResults.class, "venue", findByVenue.getText().toString());
    }

    public void findByLocationHandler(View v) {
    	find("geo.getEvents", EventResults.class, null, null);
    }

    private void find(String restAPI, Class<?> intentClass, String paramKey, String paramValue) {
        if ("".equals(paramValue)) {
        	return;
        }

        Map<String, String> params = null;
        if (paramKey != null) {
        	params = new HashMap<String, String>();
            params.put(paramKey, URLEncoder.encode(paramValue));
        }

        Uri uri = EyekabobHelper.LastFM.getUri(restAPI, params);
        Intent intent = new Intent(getApplicationContext(), intentClass);
        intent.setData(uri);
        startActivity(intent);
    }
}

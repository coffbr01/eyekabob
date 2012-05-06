/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.eyekabob.util.EyekabobHelper;

public class FindMusic extends EyekabobActivity {
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
        findByArtist.requestFocus();

        SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
        distance.setProgress(10);
        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {}
			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				TextView miles = (TextView)findViewById(R.id.milesTextView);

				if (progress < 1) {
					progress = 1;
					SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
					distance.setProgress(progress);
				}

				miles.setText(Integer.toString(progress));
			}
		});
    }

    public void findByArtistHandler(View v) {
        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        find("artist.search", ArtistList.class, "artist", findByArtist.getText().toString());
    }

    public void findByVenueHandler(View v) {
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        find("venue.search", VenueList.class, "venue", findByVenue.getText().toString());
    }

    public void findByLocationHandler(View v) {
    	Map<String, String> params = new HashMap<String, String>();

    	SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
    	int miles = distance.getProgress();
    	int km = (int)(miles * 1.609344);
    	params.put("distance", Integer.toString(km));

		Location location = EyekabobHelper.getLocation(this);
		if (location != null) {
    		params.put("lat", Double.toString(location.getLatitude()));
    		params.put("long", Double.toString(location.getLongitude()));
		}

    	find("geo.getEvents", EventList.class, params);
    }

    public void findByZipHandler(View v) {
    	EditText locationInput = (EditText)findViewById(R.id.findByLocationInput);
    	if ("".equals(locationInput.getText().toString().trim())) {
    		// Nothing entered and not using current location.
    		Toast.makeText(getApplicationContext(), R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
    		return;
    	}

		String criterion = locationInput.getText().toString().trim();
		Pattern pattern = Pattern.compile("^\\d{5}(-\\d{4})?$");
		Matcher matcher = pattern.matcher(criterion);

		if (matcher.find()) {
			findByZip(criterion);
		}
		else {
			Toast.makeText(getApplicationContext(), R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
		}

		return;
    }

    public void useCurrentLocationHandler(View v) {
    	// When the checkbox is checked, the slider should be visible and the text input should be invisible.
    	// When the checkbox is unchecked, the slider should be invisible and the text input should be visible.
    	LinearLayout distanceSeekLayout = (LinearLayout)findViewById(R.id.findSeekBarLayout);
    	LinearLayout distanceTextLayout = (LinearLayout)findViewById(R.id.findByDistanceLayout);
    	LinearLayout zipLayout = (LinearLayout)findViewById(R.id.findByZipLayout);
    	boolean isChecked = ((CheckBox)v).isChecked();
    	distanceSeekLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    	distanceTextLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    	zipLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
    }

    private void findByZip(String zip) {
    	Uri uri = EyekabobHelper.GeoNames.getUri(zip);
    	Intent intent = new Intent(getApplicationContext(), EventList.class);
    	intent.setData(uri);
    	intent.putExtra("zip", zip);
    	startActivity(intent);
    }

    private void find(String restAPI, Class<?> intentClass, Map<String, String> params) {
        Uri uri = EyekabobHelper.LastFM.getUri(restAPI, params);
        Intent intent = new Intent(getApplicationContext(), intentClass);
        intent.setData(uri);
        startActivity(intent);
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

        find(restAPI, intentClass, params);
    }

    public void adHandler(View v) {
    	EyekabobHelper.launchEmail(this);
    }
}

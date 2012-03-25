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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

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
        find("artist.search", ArtistResults.class, "artist", findByArtist.getText().toString());
    }

    public void findByVenueHandler(View v) {
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        find("venue.search", VenueResults.class, "venue", findByVenue.getText().toString());
    }

    public void findByLocationHandler(View v) {
    	Map<String, String> params = new HashMap<String, String>();

    	SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
    	int miles = distance.getProgress();
    	int km = (int)(miles * 1.609344);
    	params.put("distance", Integer.toString(km));

    	CheckBox useCurrentLocationInput = (CheckBox)findViewById(R.id.useCurrentLocationCheckBox);
    	EditText locationInput = (EditText)findViewById(R.id.findByLocationInput);
    	if (useCurrentLocationInput.isChecked()) {
    		Location location = EyekabobHelper.getLocation(this);
    		if (location != null) {
	    		params.put("lat", Double.toString(location.getLatitude()));
	    		params.put("long", Double.toString(location.getLongitude()));
    		}
    	}
    	else if (!"".equals(locationInput.getText().toString().trim())) {
    		String criterion = locationInput.getText().toString().trim();
			Pattern pattern = Pattern.compile("^\\d{5}(-\\d{4})?$");
			Matcher matcher = pattern.matcher(criterion);

			if (matcher.find()) {
				findByZip(criterion, params.get("distance"));
			}
			else {
				Toast.makeText(getApplicationContext(), R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
			}

			return;
    	}
    	else {
    		// Nothing entered and not using current location.
    		Toast.makeText(getApplicationContext(), R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
    		return;
    	}

    	find("geo.getEvents", EventResults.class, params);
    }

    public void useCurrentLocationHandler(View v) {
    	EditText location = (EditText)findViewById(R.id.findByLocationInput);
    	location.setEnabled(!((CheckBox)v).isChecked());
    }

    private void findByZip(String zip, String distance) {
    	Uri uri = EyekabobHelper.GeoNames.getUri(zip);
    	Intent intent = new Intent(getApplicationContext(), EventResults.class);
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
    	Intent emailIntent = new Intent(Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	String to[] = {"create@philipjordandesign.com", "christopherrobertfarrow@gmail.com", "coffbr01@gmail.com"};
    	emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
    	emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Eyekabob Advertising");
    	startActivity(Intent.createChooser(emailIntent, "Write email with:"));
    }
}

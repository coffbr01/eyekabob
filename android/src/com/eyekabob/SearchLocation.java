/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.eyekabob.util.EyekabobHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchLocation extends EyekabobActivity {
    private static final int DEFAULT_SEARCH_RADIUS = 10; // miles
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlocationactivity);

        EditText findByLocation = (EditText)findViewById(R.id.findByLocationInput);
        findByLocation.requestFocus();

        SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
        TextView miles = (TextView)findViewById(R.id.milesTextView);
        distance.setProgress(DEFAULT_SEARCH_RADIUS);
        miles.setText(String.valueOf(DEFAULT_SEARCH_RADIUS));
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

        // Set up state spinner.
        Spinner stateSpinner = (Spinner)findViewById(R.id.findByLocationStateInput);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);

        toggleGPSWarning();
    }

    @Override
    public void onResume() {
        super.onResume();
        toggleGPSWarning();
    }

    /**
     * Enable or disable GPS warning depending on whether GPS is enabled
     * and whether the "use current location" checkbox is checked.
     */
    private void toggleGPSWarning() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LinearLayout gpsWarning = (LinearLayout)findViewById(R.id.gpsWarning);
        boolean gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        CheckBox useCurrentLocationCheckbox = (CheckBox)findViewById(R.id.useCurrentLocationCheckBox);
        boolean isChecked = useCurrentLocationCheckbox.isChecked();

        boolean shouldShowCheckbox = isChecked && !gpsEnabled;
        gpsWarning.setVisibility(shouldShowCheckbox ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    public void findByCityHandler(View v) {
        findByCityHandler();
    }

    private void findByCityHandler() {
        Spinner stateSpinner = (Spinner)findViewById(R.id.findByLocationStateInput);
        EditText cityInput = (EditText)findViewById(R.id.findByLocationCityInput);
        Intent intent = new Intent(this, EventList.class);
        intent.putExtra("state", (String)stateSpinner.getSelectedItem());
        intent.putExtra("city", cityInput.getText().toString());
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void findByLocationHandler(View v) {
        findByLocationHandler();
    }

    private void findByLocationHandler() {
        Map<String, String> params = new HashMap<String, String>();

        SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
        int miles = distance.getProgress();
        int km = getSearchRadiusInKilometers(miles);
        params.put("distance", Integer.toString(km));

        Location location = EyekabobHelper.getLocation(this);
        if (location != null) {
            params.put("lat", Double.toString(location.getLatitude()));
            params.put("long", Double.toString(location.getLongitude()));
        }

        find("geo.getEvents", EventList.class, params);
    }

    private int getSearchRadiusInKilometers(int miles) {
        return (int)(miles * 1.609344);
    }

    @SuppressWarnings("unused")
    public void findByZipHandler(View v) {
        findByZipHandler();
    }

    private void findByZipHandler() {
        EditText locationInput = (EditText)findViewById(R.id.findByLocationInput);
        if ("".equals(locationInput.getText().toString().trim())) {
            // Nothing entered and not using current location.
            Toast.makeText(this, R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
            return;
        }

        String criterion = locationInput.getText().toString().trim();
        Pattern pattern = Pattern.compile("^\\d{5}(-\\d{4})?$");
        Matcher matcher = pattern.matcher(criterion);

        if (matcher.find()) {
            findByZip(criterion);
        }
        else {
            Toast.makeText(this, R.string.no_zip_entered, Toast.LENGTH_SHORT).show();
        }
    }

    public void useCurrentLocationHandler(View v) {
        // When the checkbox is checked the text input should be invisible.
        // When the checkbox is unchecked the text input should be visible.
        LinearLayout zipLayout = (LinearLayout)findViewById(R.id.findByZipLayout);
        ImageButton findByLocationButton = (ImageButton)findViewById(R.id.findByLocationButton);
        boolean isChecked = ((CheckBox)v).isChecked();
        zipLayout.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        findByLocationButton.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        toggleGPSWarning();
    }

    private void findByZip(String zip) {
        Uri uri = EyekabobHelper.GeoNames.getUri(zip);
        Intent intent = new Intent(this, EventList.class);
        intent.setData(uri);

        SeekBar distance = (SeekBar)findViewById(R.id.milesSeekBar);
        int miles = distance.getProgress();
        int km = getSearchRadiusInKilometers(miles);
        intent.putExtra("distance", Integer.toString(km));
        intent.putExtra("zip", zip);
        startActivity(intent);
    }

    private void find(String restAPI, Class<?> intentClass, Map<String, String> params) {
        Uri uri = EyekabobHelper.LastFM.getUri(restAPI, params);
        Intent intent = new Intent(this, intentClass);
        intent.setData(uri);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void handleGPSSettingsClick(View v) {
        handleGPSSettingsClick();
    }

    private void handleGPSSettingsClick() {
        Log.d(getClass().getName(), "GPS settings button clicked");
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }
}

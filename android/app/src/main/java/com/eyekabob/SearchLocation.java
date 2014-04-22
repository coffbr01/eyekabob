/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.eyekabob.util.EyekabobHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.OnClickListener;

public class SearchLocation extends EyekabobActivity {
    private static final int DEFAULT_SEARCH_RADIUS = 10; // miles
    private CheckBox currentLocationCheckBox;
    private CheckBox cityStateCheckbox;
    private CheckBox zipCheckbox;
    private EditText cityView;
    private Spinner stSpinner;
    private EditText zipView;
    private SeekBar distanceSeekbar;
    private TextView currentTextView;
    InputMethodManager imm;

    private OnClickListener checkboxListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if (view.getId() == currentLocationCheckBox.getId()) {
                currentLocationCheckBox.setChecked(true);
                cityStateCheckbox.setChecked(false);
                zipCheckbox.setChecked(false);
                cityView.setEnabled(false);
                imm.hideSoftInputFromWindow(cityView.getWindowToken(), 0);
                stSpinner.setEnabled(false);
                zipView.setEnabled(false);
                imm.hideSoftInputFromWindow(zipView.getWindowToken(), 0);
                distanceSeekbar.setEnabled(true);
                currentTextView.setTextColor(getResources().getColor(R.color.textcolor_white));
                currentTextView.setTextSize(20);
            }
            else if (view.getId() == cityStateCheckbox.getId()) {
                cityStateCheckbox.setChecked(true);
                currentLocationCheckBox.setChecked(false);
                zipCheckbox.setChecked(false);
                cityView.setEnabled(true);
                stSpinner.setEnabled(true);
                // Disable the zip view and close the keyboard if it's open
                zipView.setEnabled(false);
                imm.hideSoftInputFromWindow(zipView.getWindowToken(), 0);
                distanceSeekbar.setEnabled(false);
                currentTextView.setTextColor(getResources().getColor(R.color.textcolor_grey));
                currentTextView.setTextSize(14);
                imm.hideSoftInputFromWindow(zipView.getWindowToken(), 0);
            }
            else if (view.getId() == zipCheckbox.getId()) {
                zipCheckbox.setChecked(true);
                currentLocationCheckBox.setChecked(false);
                cityStateCheckbox.setChecked(false);
                // Disable the city view and close the keyboard if it's open
                cityView.setEnabled(false);
                imm.hideSoftInputFromWindow(cityView.getWindowToken(), 0);
                stSpinner.setEnabled(false);
                zipView.setEnabled(true);
                distanceSeekbar.setEnabled(true);
                currentTextView.setTextColor(getResources().getColor(R.color.textcolor_grey));
                currentTextView.setTextSize(14);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchlocationactivity);

        EditText findByLocation = (EditText)findViewById(R.id.findByLocationZipInput);
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

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // Initiate all the Views for toggling purposes
        cityView = (EditText)findViewById(R.id.findByLocationCityInput);
        stSpinner = (Spinner)findViewById(R.id.findByLocationStateInput);
        zipView = (EditText)findViewById(R.id.findByLocationZipInput);
        currentTextView = (TextView)findViewById(R.id.useCurrentLocationText);
        currentLocationCheckBox = (CheckBox)findViewById(R.id.useCurrentLocationCheckbox);
        cityStateCheckbox = (CheckBox)findViewById(R.id.useCityStateCheckbox);
        zipCheckbox = (CheckBox)findViewById(R.id.useZipCheckbox);
        distanceSeekbar = (SeekBar)findViewById(R.id.milesSeekBar);
        currentLocationCheckBox.setOnClickListener(checkboxListener);
        cityStateCheckbox.setOnClickListener(checkboxListener);
        zipCheckbox.setOnClickListener(checkboxListener);
        currentLocationCheckBox.setChecked(true);
        cityView.setEnabled(false);
        stSpinner.setEnabled(false);
        zipView.setEnabled(false);

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

        boolean isChecked = currentLocationCheckBox.isChecked();

        boolean shouldShowCheckbox = isChecked && !gpsEnabled;
        gpsWarning.setVisibility(shouldShowCheckbox ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    public void findByLocationHandler(View v) {
        findByLocationHandler();
    }

    private void findByLocationHandler() {
        // Determine which mode of search was selected
        if (currentLocationCheckBox.isChecked()) {
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
        } else if (zipCheckbox.isChecked()) {
            EditText locationInput = (EditText)findViewById(R.id.findByLocationZipInput);
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
        } else if (cityStateCheckbox.isChecked()) {
            Spinner stateSpinner = (Spinner)findViewById(R.id.findByLocationStateInput);
            EditText cityInput = (EditText)findViewById(R.id.findByLocationCityInput);
            Intent intent = new Intent(this, EventList.class);
            intent.putExtra("state", (String)stateSpinner.getSelectedItem());
            intent.putExtra("city", cityInput.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.select_search_option, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private int getSearchRadiusInKilometers(int miles) {
        return (int)(miles * 1.609344);
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
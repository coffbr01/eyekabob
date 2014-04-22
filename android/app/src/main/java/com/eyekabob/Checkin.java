/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

public class Checkin extends EyekabobActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin);
        Venue venue = (Venue)getIntent().getExtras().get("venue");
        EditText venueName = (EditText)findViewById(R.id.checkinVenue);
        venueName.setText(venue.getName());
        EditText shout = (EditText)findViewById(R.id.checkinShout);
        shout.requestFocus();
    }
    public void submitButtonHandler(View v) {
        String venueName = ((EditText)findViewById(R.id.checkinVenue)).getText().toString();
        if (venueName == null || "".equals(venueName.trim())) {
            Toast.makeText(this, "Venue name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        Venue venue = (Venue)getIntent().getExtras().get("venue");
        params.put("venueId", String.valueOf(venue.getId()));
        params.put("venue", URLEncoder.encode(venueName));
        String shout = ((EditText)findViewById(R.id.checkinShout)).getText().toString();
        params.put("shout", URLEncoder.encode(shout));
        String uri = EyekabobHelper.Foursquare.getUri("checkins/add", params);

        new JSONRequestTask("POST").execute(uri);
    }
    protected class JSONRequestTask extends JSONTask {
        public JSONRequestTask(String requestType) {
            super(requestType);
        }
        protected void onPreExecute() {
            Checkin.this.createDialog(R.string.checking_in);
            Checkin.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            Checkin.this.dismissDialog();

            if (result == null) {
                Toast.makeText(Checkin.this, "Error while checking in.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(Checkin.this, "Checked in successfully!", Toast.LENGTH_SHORT).show();
            Checkin.this.startActivity(new Intent(Checkin.this, Home.class));
        }
    }
}

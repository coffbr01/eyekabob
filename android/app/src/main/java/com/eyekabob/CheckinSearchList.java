/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.adapters.VenueListAdapter;
import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

public class CheckinSearchList extends EyekabobActivity {

    private OnItemClickListener listItemListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Venue venue = (Venue)parent.getAdapter().getItem(position);
            Intent checkinIntent = new Intent(CheckinSearchList.this, Checkin.class);
            checkinIntent.putExtra("venue", venue);
            CheckinSearchList.this.startActivity(checkinIntent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adlistactivity);
        VenueListAdapter adapter = new VenueListAdapter(getApplicationContext());
        ListView lv = (ListView)findViewById(R.id.adList);
        lv.setOnItemClickListener(listItemListener);
        lv.setAdapter(adapter);
        Bundle extras = (Bundle)getIntent().getExtras();
        Location location = null;

        if (extras != null) {
            location = (Location)extras.get("location");
        }

        if (location == null) {
            location = EyekabobHelper.getLocation(this);
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("ll", location.getLatitude() + "," + location.getLongitude());
        params.put("radius", "3000"); // 3km seems like a reasonable checkin distance.

        String uri = EyekabobHelper.Foursquare.getUri("venues/search", params);

        new JSONRequestTask().execute(uri);
    }

    public void loadData(JSONObject data) {
        try {
            JSONObject response = data.getJSONObject("response");
            ListView lv = (ListView)findViewById(R.id.adList);
            VenueListAdapter adapter = (VenueListAdapter)lv.getAdapter();
            JSONArray venues = response.getJSONArray("venues");
            for (int i = 0; i < venues.length(); i++) {
                JSONObject placeData = venues.getJSONObject(i);
                Venue venue = new Venue();
                venue.setName(placeData.optString("name"));
                venue.setId(placeData.optString("id"));
                JSONObject location = placeData.optJSONObject("location");
                if (location != null) {
                    venue.setCity(location.optString("city"));
                    venue.setStreet(location.optString("address"));
                    venue.setPostalCode(location.optString("postalCode"));
                    venue.setCountry(location.optString("country"));
                }
                adapter.add(venue);
            }
        }
        catch (JSONException e) {
            Toast.makeText(this, "Could not get nearby places", Toast.LENGTH_SHORT).show();
            Log.e(getClass().getName(), "Unable to parse JSON response", e);
        }
    }

    public class JSONRequestTask extends JSONTask {
        protected void onPreExecute() {
            CheckinSearchList.this.createDialog(R.string.searching);
            CheckinSearchList.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            CheckinSearchList.this.dismissDialog();

            if (result == null) {
                Toast.makeText(CheckinSearchList.this, "Error during facebook place search.", Toast.LENGTH_SHORT).show();
                return;
            }

            CheckinSearchList.this.loadData(result);
        }
    }
}

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.adapters.VenueListAdapter;
import com.eyekabob.models.Venue;
import com.eyekabob.util.JSONTask;

public class VenueList extends EyekabobActivity {
    private VenueListAdapter adapter;
    private OnItemClickListener listItemListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Venue venue = (Venue)parent.getAdapter().getItem(position);
            Intent intent = new Intent(getApplicationContext(), VenueInfo.class);
            intent.putExtra("venue", venue);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adlistactivity);
        adapter = new VenueListAdapter(getApplicationContext());
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());
        ListView lv = (ListView)findViewById(R.id.adList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listItemListener);
    }
    
    protected void loadVenues(JSONObject response) {
        try {
            JSONArray venues = response.getJSONObject("results").getJSONObject("venuematches").getJSONArray("venue");
            if (venues.length() == 0) {
                Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
                return;
            }
            for (int i = 0; i < venues.length(); i++) {
                JSONObject venue = venues.getJSONObject(i);
                JSONObject location = venue.getJSONObject("location");
    
                Venue venueRow = new Venue();
    
                venueRow.setId(venue.optString("id"));
                venueRow.setName(venue.optString("name"));
                venueRow.setUrl(venue.optString("url"));
    
                venueRow.setCity(location.optString("city"));
                venueRow.setCountry(location.optString("country"));
                venueRow.setStreet(location.optString("street"));
    
                JSONObject geoPoint = venue.optJSONObject("geo:point");
                if (geoPoint != null) {
                    venueRow.setLat(geoPoint.optString("geo:lat"));
                    venueRow.setLon(geoPoint.optString("geo:long"));
                }

                adapter.add(venueRow);
            }
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends JSONTask {
        protected void onPreExecute() {
            VenueList.this.createDialog(R.string.searching);
            VenueList.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            VenueList.this.dismissDialog();
            VenueList.this.loadVenues(result);
        }
    }
}

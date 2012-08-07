/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.eyekabob.adapters.EventListAdapter;
import com.eyekabob.models.Event;
import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventList extends EyekabobActivity {
    // A map whose keys are zip codes and values are lat/long values delimited by "|"
    // An example entry would be: {"50210":"42.5239|83.2233"}
    private static final Map<String, String> zipToGeoMap = new HashMap<String, String>();
    EventListAdapter adapter;

    private OnItemClickListener listItemListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getAdapter().getItem(position);
            Intent intent = new Intent(getApplicationContext(), EventInfo.class);
            intent.putExtra("event", event);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adlistactivity);
        adapter = new EventListAdapter(getApplicationContext());
        ListView lv = (ListView)findViewById(R.id.adList);
        lv.setAdapter(adapter);
        Uri uri = this.getIntent().getData();
        if (getIntent().hasExtra("zip")) {
            // Search by zip.
            String zip = getIntent().getExtras().getString("zip");
            if (zip != null) {
                zip = zip.trim();
            }
            if (zipToGeoMap.containsKey(zip)) {
                Log.d(getClass().getName(), "Using cached zip");
                String[] latLong = zipToGeoMap.get(zip).split("\\|");
                Uri lastFMURI = getLastFMURI(latLong[0], latLong[1]);
                sendLastFMRequest(lastFMURI.toString());
            }
            else {
                Log.d(getClass().getName(), "Getting zip from geonames service");
                sendJSONRequest(uri.toString());
            }
        }
        else {
            Log.d(getClass().getName(), "Searching for events using current location");
            sendLastFMRequest(uri.toString());
        }

        lv.setOnItemClickListener(listItemListener);
    }

    @Override
    public void onDestroy() {
        adapter.clearCache();
        super.onDestroy();
    }

    protected void loadEvents(JSONObject response) {
        try {
            JSONObject jsonEvents = response.optJSONObject("events");
            if (jsonEvents == null) {
                Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
                return;
            }

            Object eventsObj = jsonEvents.get("event");
            JSONArray events;
            if (eventsObj instanceof JSONArray) {
                events = (JSONArray)eventsObj;
            }
            else {
                // For some incredibly stupid reason, a one item list is not a list.
                // So create a list and stick the one item in it.
                events = new JSONArray();
                events.put(eventsObj);
            }

            for (int i = 0; i < events.length(); i++) {
                Event event = new Event();
                Venue venue = new Venue();
                event.setVenue(venue);
                JSONObject jsonEvent = events.getJSONObject(i);
                JSONObject jsonVenue = jsonEvent.getJSONObject("venue");
                JSONObject jsonLocation = jsonVenue.getJSONObject("location");
                JSONObject jsonGeo = jsonLocation.optJSONObject("geo:point");

                event.setId(jsonEvent.getString("id"));
                event.setName(jsonEvent.getString("title"));
                event.setDate(EyekabobHelper.LastFM.toReadableDate(jsonEvent.getString("startDate")));
                JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage("large", jsonEvent.getJSONArray("image"));
                event.addImageURL("large", jsonImage.getString("#text"));

                venue.setName(jsonVenue.getString("name"));
                venue.setCity(jsonLocation.getString("city"));

                if (jsonGeo != null) {
                    venue.setLat(jsonGeo.optString("geo:lat"));
                    venue.setLon(jsonGeo.getString("geo:long"));
                }

                adapter.add(event);
            }
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
    }

    protected void sendJSONRequest(String uri) {
        new GeoNamesJSONRequestTask().execute(uri);
    }

    protected void sendLastFMRequest(String uri) {
        new LastFMRequestTask().execute(uri);
    }

    protected Uri getLastFMURI(String latitude, String longitude) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("long", longitude);
        String distance = getIntent().getExtras().getString("distance");
        if (distance != null) {
            params.put("distance", distance);
        }
        return EyekabobHelper.LastFM.getUri("geo.getEvents", params);
    }

    public void adHandler(View v) {
        EyekabobHelper.launchEmail(this);
    }

    /**
     * Sends request to the GeoNames service to get information about
     * a given ZIP code. The pieces of information this app uses are
     * latitude and longitude of a given zip.
     */
    private class GeoNamesJSONRequestTask extends JSONTask {
        protected void onPreExecute() {
            EventList.this.createDialog(R.string.searching);
            EventList.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            String latitude;
            String longitude;
            try {
                JSONArray locations = (JSONArray)result.get("postalcodes");
                JSONObject jsonLocation = (JSONObject)locations.get(0);
                latitude = jsonLocation.getString("lat");
                longitude = jsonLocation.getString("lng");
                String zip = jsonLocation.getString("postalcode");
                zipToGeoMap.put(zip, latitude + "|" + longitude);
            }
            catch (JSONException e) {
                Log.e(getClass().getName(), "Unable to get geo information for zip", e);
                Toast.makeText(EventList.this, R.string.zip_error, Toast.LENGTH_LONG).show();
                return;
            }

            Uri uri = EventList.this.getLastFMURI(latitude, longitude);
            EventList.this.sendLastFMRequest(uri.toString());
        }
    }

    // Handles the asynchronous request, away from the UI thread.
    private class LastFMRequestTask extends JSONTask {
        protected void onPreExecute() {
            EventList.this.createDialog(R.string.searching);
            EventList.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            EventList.this.dismissDialog();
            EventList.this.loadEvents(result);
        }
    }
}

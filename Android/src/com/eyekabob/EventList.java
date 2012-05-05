/*
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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.models.Event;
import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

public class EventList extends EyekabobActivity {
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
        	if (EyekabobHelper.zipToNameMap.containsKey(zip)) {
        		Log.d(getClass().getName(), "Using cached zip");
        		Uri lastFMURI = getLastFMURI(EyekabobHelper.zipToNameMap.get("zip"));
        		sendDocumentRequest(lastFMURI.toString());
        	}
        	else {
        		Log.d(getClass().getName(), "Getting zip from geonames service");
        		sendJSONRequest(uri.toString());
        	}

        	// Don't show distance for zip search.
        	getIntent().putExtra("showDistance", false);
        }
        else {
        	Log.d(getClass().getName(), "Searching for events using current location");
        	sendDocumentRequest(uri.toString());
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
	    	JSONArray events = response.getJSONObject("events").getJSONArray("event");
	    	if (events.length() == 0) {
	    		Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
	    		return;
	    	}
	
	    	for (int i = 0; i < events.length(); i++) {
	    		Event event = new Event();
	    		Venue venue = new Venue();
	    		event.setVenue(venue);
	    		JSONObject jsonEvent = events.getJSONObject(i);
	    		jsonEvent.getString("title");
	    		jsonEvent.getString("id");
	    		JSONObject jsonVenue = jsonEvent.getJSONObject("venue");
	    		JSONObject jsonLocation = jsonVenue.getJSONObject("location");
	    		JSONObject jsonGeo = jsonLocation.optJSONObject("geo:point");
	    		event.setDate(EyekabobHelper.LastFM.toReadableDate(jsonEvent.getString("startDate")));
	
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
    		throw new RuntimeException(e);
    	}
    }

    protected void sendJSONRequest(String uri) {
    	new JSONRequestTask().execute(uri);
    }

    protected void sendDocumentRequest(String uri) {
    	new DocumentRequestTask().execute(uri);
    }

    protected Uri getLastFMURI(String location) {
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("location", location);
    	params.put("distance", getIntent().getExtras().getString("distance"));
    	return EyekabobHelper.LastFM.getUri("geo.getEvents", params);
    }

    public void adHandler(View v) {
    	EyekabobHelper.launchEmail(this);
    }

    private class JSONRequestTask extends JSONTask {
    	protected void onPreExecute() {
    		EventList.this.createDialog(R.string.searching);
    		EventList.this.showDialog();
    	}
    	protected void onPostExecute(JSONObject result) {
    		String location = null;
    		try {
        		JSONArray locations = (JSONArray)result.get("postalcodes");
        		JSONObject jsonLocation = (JSONObject)locations.get(0);
        		location = (String)jsonLocation.get("placeName");
    		}
    		catch (JSONException e) {
    			e.printStackTrace();
    		}

    		Uri uri = EventList.this.getLastFMURI(location);
    		EventList.this.sendDocumentRequest(uri.toString());
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class DocumentRequestTask extends JSONTask {
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

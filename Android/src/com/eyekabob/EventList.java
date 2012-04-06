package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;
import com.eyekabob.util.NiceNodeList;

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
    
    protected void loadEvents(Document doc) {
    	if (doc == null) {
    		Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
    		return;
    	}
    	NodeList events = doc.getElementsByTagName("event");
    	if (events.getLength() == 0) {
    		Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
    		return;
    	}

    	for (int i = 0; i < events.getLength(); i++) {
    		Event row = new Event();
    		Node eventNode = events.item(i);
    		NiceNodeList eventNodeList = new NiceNodeList(eventNode.getChildNodes());
    		Map<String, Node> eventNodes = eventNodeList.get("title", "id", "venue", "startDate", "image");

    		NiceNodeList venueNodeList = new NiceNodeList(eventNodes.get("venue").getChildNodes());
    		Map<String, Node> venueNodes = venueNodeList.get("name", "location");

    		NiceNodeList locationNodeList = new NiceNodeList(venueNodes.get("location").getChildNodes());
    		Map<String, Node> locationNodes = locationNodeList.get("city", "geo:point");

    		row.setId(eventNodes.get("id").getTextContent());
    		row.addImageURL("large", eventNodes.get("image").getTextContent());

    		if (!getIntent().hasExtra("showDistance") || getIntent().getExtras().getBoolean("showDistance", true)) {
	    		NiceNodeList geoNodeList = new NiceNodeList(locationNodes.get("geo:point").getChildNodes());
	    		Map<String, Node> geoNodes = geoNodeList.get("geo:lat", "geo:long");
	    		Node lat = geoNodes.get("geo:lat");
	    		Node lon = geoNodes.get("geo:long");

	    		if (lat != null && lon != null) {
	    			String latStr = lat.getTextContent();
	    			String lonStr = lon.getTextContent();
	    			if (!"".equals(latStr) && !"".equals(lonStr)) {
	    				row.setLat(latStr);
	    				row.setLon(lonStr);
	    			}
	    		}
    		}

    		row.setName(eventNodes.get("title").getTextContent());
    		row.setDate(EyekabobHelper.LastFM.toReadableDate(eventNodes.get("startDate").getTextContent()));

    		if (!getIntent().hasExtra("showVenue") || getIntent().getExtras().getBoolean("showVenue", true)) {
    			row.setVenue(venueNodes.get("name").getTextContent());
    		}

    		if (!getIntent().hasExtra("showCity") || getIntent().getExtras().getBoolean("showCity", true)) {
    			row.setCity(locationNodes.get("city").getTextContent());
    		}

		    adapter.add(row);
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
    private class DocumentRequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		EventList.this.createDialog(R.string.searching);
    		EventList.this.showDialog();
    	}
    	protected void onPostExecute(Document result) {
    		EventList.this.dismissDialog();
    		EventList.this.loadEvents(result);
    	}
    }
}

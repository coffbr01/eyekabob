package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.JSONTask;
import com.eyekabob.util.LastFMUtil;

public class EventResults extends ListActivity {
	private Dialog alertDialog;
	ArrayAdapter<String> adapter;
	private Map<String, String> eventMap;
	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String event = ((TextView) view).getText().toString();
			Intent intent = new Intent(getApplicationContext(), Event.class);
			Map<String, String> params = new HashMap<String, String>();
			params.put("event", eventMap.get(event));
			intent.setData(EyekabobHelper.LastFM.getUri("event.getInfo", params));
			startActivity(intent);
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
        setListAdapter(adapter);
        Uri uri = this.getIntent().getData();
        if (uri.getHost().contains("geonames")) {
        	String zip = this.getIntent().getExtras().getString("zip");
        	if (EyekabobHelper.zipToNameMap.containsKey(zip)) {
        		Uri lastFMURI = getLastFMURI(EyekabobHelper.zipToNameMap.get("zip"));
        		sendDocumentRequest(lastFMURI.toString());
        	}
        	else {
        		sendJSONRequest(uri.toString());
        	}
        }
        else {
        	sendDocumentRequest(uri.toString());
        }

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(listItemListener);
    }

    // TODO: use dialogfragment to show dialog
    protected void createDialog() {
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.searching);
	    builder.setCancelable(false);
	    alertDialog = builder.create();
	    alertDialog.setOwnerActivity(this);
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
    	eventMap = new HashMap<String, String>();
    	for (int i = 0; i < events.getLength(); i++) {
    		String title = "";
    		String venue = "";
    		String startDate = "";
    		String startTime = "";
    		String id = "";
    		String city = "";
    		String distance = "";
    		Node eventNode = events.item(i);
    		NodeList eventChildren = eventNode.getChildNodes();
    		for (int j = 0; j < eventChildren.getLength(); j++) {
    		    Node eventChildNode = eventChildren.item(j);
    		    String nodeName = eventChildNode.getNodeName();
    		    if ("title".equals(nodeName)) {
    		    	title = eventChildNode.getTextContent();
    		    }
    		    else if ("id".equals(nodeName)) {
    		    	id = eventChildNode.getTextContent();
    		    }
    		    else if ("venue".equals(nodeName)) {
    		    	NodeList venueChildren = eventChildNode.getChildNodes();
    		    	for (int k = 0; k < venueChildren.getLength(); k++) {
    		    		if ("name".equals(venueChildren.item(k).getNodeName())) {
    		    			venue = venueChildren.item(k).getTextContent();
    		    		}
    		    		else if ("location".equals(venueChildren.item(k).getNodeName())) {
    		    			NodeList locationChildren = venueChildren.item(k).getChildNodes();
    		    			for (int l = 0; l < locationChildren.getLength(); l++) {
    		    				if ("city".equals(locationChildren.item(l).getNodeName())) {
    		    					city = locationChildren.item(l).getTextContent();
    		    				}
    		    				else if ("geo:point".equals(locationChildren.item(l).getNodeName())) {
    		    					NodeList geoChildren = locationChildren.item(l).getChildNodes();
    		    					String lat = "";
    		    					String lon = "";
    		    					for (int m = 0; m < geoChildren.getLength(); m++) {
    		    						if ("geo:lat".equals(geoChildren.item(m).getNodeName())) {
    		    							lat = geoChildren.item(m).getTextContent();
    		    						}
    		    						if ("geo:long".equals(geoChildren.item(m).getNodeName())) {
    		    							lon = geoChildren.item(m).getTextContent();
    		    						}
    		    					}
    		    					if (!"".equals(lat) && !"".equals(lon)) {
    		    						distance = getDistance(Double.parseDouble(lat), Double.parseDouble(lon));
    		    					}
    		    				}
    		    			}
    		    		}
    		    	}
    		    }
    		    else if ("startDate".equals(nodeName)) {
    		    	startDate = LastFMUtil.toReadableDate(eventChildNode.getTextContent());
    		    }
    		}
		    adapter.add(title + "\n" + venue + "\n" + city + "\n" + startDate + " " + startTime + distance);
		    eventMap.put(title + "\n" + venue + "\n" + city + "\n" + startDate + " " + startTime + distance, id);
    	}
    }

    public String getDistance(double lat, double lon) {
    	Location location = EyekabobHelper.getLocation(this);
    	if (location == null) {
    		return "";
    	}
    	double currentLat = location.getLatitude();
    	double currentLon = location.getLongitude();
    	int R = 3959; // Earth radius in miles.
    	double dLat = Math.toRadians(currentLat - lat);
    	double dLon = Math.toRadians(currentLon - lon);
    	lat = Math.toRadians(lat);
    	currentLat = Math.toRadians(currentLat);
    	double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat) * Math.cos(currentLat);
    	double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    	return "\n" + Math.round(R * c) + " mi";
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
    	params.put("distance", (String)getIntent().getExtras().get("distance"));
    	return EyekabobHelper.LastFM.getUri("geo.getEvents", params);
    }

    private class JSONRequestTask extends JSONTask {
    	protected void onPreExecute() {
    		if (alertDialog == null) {
        		EventResults.this.createDialog();
    		}

    		if (!alertDialog.isShowing()) {
    			alertDialog.show();
    		}
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

    		Uri uri = EventResults.this.getLastFMURI(location);
    		EventResults.this.sendDocumentRequest(uri.toString());
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class DocumentRequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		if (alertDialog == null) {
        		EventResults.this.createDialog();
    		}

    		if (!alertDialog.isShowing()) {
    			alertDialog.show();
    		}
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		EventResults.this.loadEvents(result);
    	}
    }
}

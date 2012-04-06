package com.eyekabob;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.models.Venue;
import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.NiceNodeList;

public class VenueList extends ListActivity {
	private Dialog alertDialog;
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
        adapter = new VenueListAdapter(getApplicationContext());
        setListAdapter(adapter);
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());
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
    
    protected void loadVenues(Document doc) {
    	NodeList venues = doc.getElementsByTagName("venue");
    	if (venues.getLength() == 0) {
    		Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
    		return;
    	}
    	for (int i = 0; i < venues.getLength(); i++) {
    		Node venueNode = venues.item(i);
    		NiceNodeList venueNodeList = new NiceNodeList(venueNode.getChildNodes());
    		Map<String, Node> venueNodes = venueNodeList.get("name", "id", "url", "location");

    		NiceNodeList locationNodeList = new NiceNodeList(venueNodes.get("location").getChildNodes());
    		Map<String, Node> locationNodes = locationNodeList.get("city", "country", "street", "geo:point");

    		NiceNodeList geoNodeList = new NiceNodeList(locationNodes.get("geo:point").getChildNodes());
    		Map<String, Node> geoNodes = geoNodeList.get("geo:lat", "geo:long");
    		Node lat = geoNodes.get("geo:lat");
    		Node lon = geoNodes.get("geo:long");

    		Venue venueRow = new Venue();

    		if (lat != null && lon != null) {
    			String latStr = lat.getTextContent();
    			String lonStr = lon.getTextContent();
    			if (!"".equals(latStr) && !"".equals(lonStr)) {
    				venueRow.setLat(Double.parseDouble(latStr));
    				venueRow.setLon(Double.parseDouble(lonStr));
    			}
    		}

    		venueRow.setId(Integer.parseInt(venueNodes.get("id").getTextContent()));
    		venueRow.setName(venueNodes.get("name").getTextContent());
    		venueRow.setUrl(venueNodes.get("url").getTextContent());

    		venueRow.setCity(locationNodes.get("city").getTextContent());
    		venueRow.setCountry(locationNodes.get("country").getTextContent());
    		venueRow.setStreet(locationNodes.get("street").getTextContent());

    		adapter.add(venueRow);
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		VenueList.this.createDialog();
    		alertDialog.show();
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		VenueList.this.loadVenues(result);
    	}
    }
}

package com.eyekabob;

import java.util.HashMap;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyekabob.util.LastFMTask;

public class VenueResults extends ListActivity {
	private Dialog alertDialog;
	private ArrayAdapter<String> adapter;
	private Map<String, String> venueMap;
	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String key = ((TextView) view).getText().toString();
			Intent intent = new Intent(getApplicationContext(), EventResults.class);
			Map<String, String> params = new HashMap<String, String>();
			params.put("venue", venueMap.get(key));
			intent.setData(EyekabobHelper.LastFM.getUri("venue.getEvents", params));
			startActivity(intent);
		}
	};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
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
    	venueMap = new HashMap<String, String>();
    	for (int i = 0; i < venues.getLength(); i++) {
    		Node artistNode = venues.item(i);
    		NodeList venueChildren = artistNode.getChildNodes();
    		String name = "";
    		String city = "";
    		String id = "";
    		for (int j = 0; j < venueChildren.getLength(); j++) {
    		    Node venueChildNode = venueChildren.item(j);
    		    if ("name".equals(venueChildNode.getNodeName())) {
    		    	name = venueChildNode.getTextContent();
    		    }
    		    else if ("id".equals(venueChildNode.getNodeName())) {
    		    	id = venueChildNode.getTextContent();
    		    }
    		    else if ("location".equals(venueChildNode.getNodeName())) {
    		    	NodeList locationChildren = venueChildNode.getChildNodes();
    		    	for (int k = 0; k < locationChildren.getLength(); k++) {
    		    		if ("city".equals(locationChildren.item(k).getNodeName())) {
    		    			city = locationChildren.item(k).getTextContent();
    		    		}
    		    	}
    		    }
    		}
    		adapter.add(name + "\n" + city);
    		venueMap.put(name + "\n" + city, id);
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends LastFMTask {
    	protected void onPreExecute() {
    		VenueResults.this.createDialog();
    		alertDialog.show();
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		VenueResults.this.loadVenues(result);
    	}
    }
}

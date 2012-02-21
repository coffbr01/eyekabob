package com.eyekabob;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private class RequestTask extends AsyncTask<String, Void, Document> {
    	protected void onPreExecute() {
    		VenueResults.this.createDialog();
    		alertDialog.show();
    	}
    	protected Document doInBackground(String... uris) {
    		return doRequest(uris[0]);
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		VenueResults.this.loadVenues(result);
    	}
        public Document doRequest(String uri) {
        	Document result = null;
    		try {
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			result = builder.parse(uri);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		catch (ParserConfigurationException e) {
    			e.printStackTrace();
    		}
    		catch (SAXException e) {
    			e.printStackTrace();
    		}

            return result;
        }
    }
}

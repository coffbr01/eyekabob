package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.eyekabob.util.LastFMTask;

public class Event extends Activity {
	private Dialog alertDialog;
	private Map<String, String> vendors;
	private String imageUrl;
	private String title;
	private String venue;
	private String startDate;
	private String startTime;
	private String description;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventactivity);
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());
    }
    protected void loadEvent(Document doc) {
    	Node eventNode = doc.getElementsByTagName("event").item(0);

		vendors = new HashMap<String, String>();
		NodeList eventChildren = eventNode.getChildNodes();
		for (int j = 0; j < eventChildren.getLength(); j++) {
		    Node eventChildNode = eventChildren.item(j);
		    String nodeName = eventChildNode.getNodeName();
		    if ("title".equals(nodeName)) {
		    	title = eventChildNode.getTextContent();
		    }
		    else if ("venue".equals(nodeName)) {
		    	NodeList venueChildren = eventChildNode.getChildNodes();
		    	for (int k = 0; k < venueChildren.getLength(); k++) {
		    		if ("name".equals(venueChildren.item(k).getNodeName())) {
		    			venue = venueChildren.item(k).getTextContent();
		    		}
		    	}
		    }
		    else if ("startDate".equals(nodeName)) {
		    	startDate = eventChildNode.getTextContent();
		    }
		    else if ("startTime".equals(nodeName)) {
		    	startTime = eventChildNode.getTextContent();
		    }
		    else if ("image".equals(nodeName)) {
		    	NamedNodeMap attributes = eventChildNode.getAttributes();
		    	String size = attributes.getNamedItem("size").getTextContent();
		    	if ("large".equals(size)) {
		    		imageUrl = eventChildNode.getTextContent();
		    	}
		    	else if ("".equals(imageUrl)) {
		    		imageUrl = eventChildNode.getTextContent();
		    	}
		    }
		    else if ("description".equals(nodeName)) {
		    	description = eventChildNode.getTextContent();
		    }
		    else if ("tickets".equals(nodeName)) {
		    	NodeList vendorNodes = eventChildNode.getChildNodes();
		    	for (int k = 0; k < vendorNodes.getLength(); k++) {
		    		if ("ticket".equals(vendorNodes.item(k).getNodeName())) {
		    			Node ticket = vendorNodes.item(k);
		    			NamedNodeMap attributes = ticket.getAttributes();
		    			String vendorName = attributes.getNamedItem("supplier").getTextContent();
		    			vendors.put(vendorName, ticket.getTextContent());
		    		}
		    	}
		    }
		}

		Toast.makeText(getApplicationContext(), "TODO: show event data", Toast.LENGTH_LONG).show();
    }

    // TODO: use dialogfragment to show dialog
    protected void createDialog() {
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.searching);
	    builder.setCancelable(false);
	    alertDialog = builder.create();
	    alertDialog.setOwnerActivity(this);
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends LastFMTask {
    	protected void onPreExecute() {
    		Event.this.createDialog();
    		alertDialog.show();
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		Event.this.loadEvent(result);
    	}

    }
}

package com.eyekabob;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.LastFMUtil;

public class Event extends EyekabobActivity {
	private Dialog alertDialog;
	private Map<String, String> vendors;
	private List<String> artists;
	private String startDate = "";
	private String headliner = "";
	private String imageUrl = "";
	private String title = "";
	private String venue = "";
	private String description = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventactivity);
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());

        WebView wv = (WebView)findViewById(R.id.eventDescription);
        wv.setBackgroundColor(0x00000000);
    }

    protected void loadEvent(Document doc) {
    	Node eventNode = doc.getElementsByTagName("event").item(0);

		vendors = new HashMap<String, String>();
		artists = new ArrayList<String>();
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
		    	startDate = LastFMUtil.toReadableDate(eventChildNode.getTextContent());
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
		    else if ("artists".equals(nodeName)) {
		    	NodeList artistNodes = eventChildNode.getChildNodes();
		    	for (int k = 0; k < artistNodes.getLength(); k++) {
		    		if ("artist".equals(artistNodes.item(k).getNodeName())) {
		    			artists.add(artistNodes.item(k).getTextContent());
		    		}
		    		else if ("headliner".equals(artistNodes.item(k).getNodeName())) {
		    			headliner = artistNodes.item(k).getTextContent();
		    		}
		    	}
		    	if (artists.contains(headliner)) {
		    		artists.remove(headliner);
		    	}
		    }
		}

		ImageView iv = (ImageView)findViewById(R.id.eventImageView);
		InputStream is = null;
		try {
			is = (InputStream) new URL(imageUrl).getContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bitmap img = BitmapFactory.decodeStream(is);
		iv.setImageBitmap(img);

		String rendered = "Title: " + title + "\nHeadliner: " + headliner;
		if (!artists.isEmpty()) {
			rendered += "\n  also with:";
		}
		for (String artist : artists) {
			rendered += "\n    " + artist;
		}
		rendered += "\n\nWhere: " + venue;
		rendered += "\n" + startDate;

		TextView tv = (TextView)findViewById(R.id.eventText);
		tv.append(rendered);

		WebView wv = (WebView)findViewById(R.id.eventDescription);
		description = "<div style='color:white'>" + description + "</div>";
		wv.loadData(description, "text/html", "UTF8");
    }

    // TODO: use dialogfragment to show dialog
    protected void createDialog() {
	    Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage(R.string.loading);
	    builder.setCancelable(false);
	    alertDialog = builder.create();
	    alertDialog.setOwnerActivity(this);
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends DocumentTask {
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

package com.eyekabob;

import java.io.IOException;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EventResults extends ListActivity {
	private Dialog alertDialog;
	ArrayAdapter<String> adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_item);
        setListAdapter(adapter);
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
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
    	NodeList artists = doc.getElementsByTagName("event");
    	for (int i = 0; i < artists.getLength(); i++) {
    		String title = "";
    		String venue = "";
    		String startDate = "";
    		String startTime = "";
    		Node artistNode = artists.item(i);
    		NodeList artistChildren = artistNode.getChildNodes();
    		for (int j = 0; j < artistChildren.getLength(); j++) {
    		    Node artistChildNode = artistChildren.item(j);
    		    String nodeName = artistChildNode.getNodeName();
    		    if ("title".equals(nodeName)) {
    		    	title = artistChildNode.getTextContent();
    		    }
    		    else if ("venue".equals(nodeName)) {
    		    	NodeList venueChildren = artistChildNode.getChildNodes();
    		    	for (int k = 0; k < venueChildren.getLength(); k++) {
    		    		if ("name".equals(venueChildren.item(k).getNodeName())) {
    		    			venue = venueChildren.item(k).getTextContent();
    		    		}
    		    	}
    		    }
    		    else if ("startDate".equals(nodeName)) {
    		    	startDate = artistChildNode.getTextContent();
    		    }
    		    else if ("startTime".equals(nodeName)) {
    		    	startTime = artistChildNode.getTextContent();
    		    }
    		}
		    adapter.add(title + "\n" + venue + "\n" + startDate + " " + startTime);
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends AsyncTask<String, Void, Document> {
    	protected void onPreExecute() {
    		EventResults.this.createDialog();
    		alertDialog.show();
    	}
    	protected Document doInBackground(String... uris) {
    		return doRequest(uris[0]);
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		EventResults.this.loadEvents(result);
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

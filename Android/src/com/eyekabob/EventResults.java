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
    
    protected void loadEvents(Document doc) {
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
    		    	}
    		    }
    		    else if ("startDate".equals(nodeName)) {
    		    	startDate = eventChildNode.getTextContent();
    		    }
    		    else if ("startTime".equals(nodeName)) {
    		    	startTime = eventChildNode.getTextContent();
    		    }
    		}
		    adapter.add(title + "\n" + venue + "\n" + startDate + " " + startTime);
		    eventMap.put(title + "\n" + venue + "\n" + startDate + " " + startTime, id);
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

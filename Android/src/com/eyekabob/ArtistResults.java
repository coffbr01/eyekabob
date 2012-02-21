package com.eyekabob;

import java.io.IOException;
import java.net.URLEncoder;
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

public class ArtistResults extends ListActivity {
	private Dialog alertDialog;
	ArrayAdapter<String> adapter;
	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String artist = ((TextView) view).getText().toString();
			Intent intent = new Intent(getApplicationContext(), EventResults.class);
			Map<String, String> params = new HashMap<String, String>();
			params.put("artist", URLEncoder.encode(artist));
			intent.setData(EyekabobHelper.LastFM.getUri("artist.getEvents", params));
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
    
    protected void loadArtists(Document doc) {
    	NodeList artists = doc.getElementsByTagName("artist");
    	for (int i = 0; i < artists.getLength(); i++) {
    		Node artistNode = artists.item(i);
    		NodeList artistChildren = artistNode.getChildNodes();
    		for (int j = 0; j < artistChildren.getLength(); j++) {
    		    Node artistChildNode = artistChildren.item(j);
    		    if ("name".equals(artistChildNode.getNodeName())) {
    		    	adapter.add(artistChildNode.getTextContent());
    		    }
    		}
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends AsyncTask<String, Void, Document> {
    	protected void onPreExecute() {
    		ArtistResults.this.createDialog();
    		alertDialog.show();
    	}
    	protected Document doInBackground(String... uris) {
    		return doRequest(uris[0]);
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		ArtistResults.this.loadArtists(result);
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

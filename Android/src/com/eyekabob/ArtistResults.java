package com.eyekabob;

import java.net.URLEncoder;
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

import com.eyekabob.util.DocumentTask;

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
    	if (artists.getLength() == 0) {
    		Toast.makeText(getApplicationContext(), R.string.no_results, Toast.LENGTH_LONG).show();
    		return;
    	}
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
    private class RequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		ArtistResults.this.createDialog();
    		alertDialog.show();
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		ArtistResults.this.loadArtists(result);
    	}
    }
}

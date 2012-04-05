package com.eyekabob;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.models.Artist;
import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.NiceNodeList;

public class ArtistList extends EyekabobActivity {
	private Dialog alertDialog;
	ArtistListAdapter adapter;
	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Artist artist = (Artist)parent.getAdapter().getItem(position);
			Intent intent = new Intent(getApplicationContext(), ArtistInfo.class);
			intent.putExtra("artist", artist.getName());
			startActivity(intent);
		}
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adlistactivity);
        adapter = new ArtistListAdapter(getApplicationContext());
        ListView lv = (ListView)findViewById(R.id.adList);
        lv.setAdapter(adapter);
        Uri uri = this.getIntent().getData();
        new RequestTask().execute(uri.toString());
        lv.setOnItemClickListener(listItemListener);
    }

    @Override
    public void onDestroy() {
    	adapter.clearCache();
    	super.onDestroy();
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
    		NiceNodeList artistNodeList = new NiceNodeList(artistNode.getChildNodes());
    		Map<String, Node> artistNodes = artistNodeList.get("name", "mbid", "url", "image");
    		Artist artist = new Artist();
    		artist.setName(artistNodes.get("name").getTextContent());
    		artist.setMbid(artistNodes.get("mbid").getTextContent());
    		artist.setUrl(artistNodes.get("url").getTextContent());
    		artist.addImageURL("large", artistNodes.get("image").getTextContent());
    		adapter.add(artist);
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		ArtistList.this.createDialog();
    		alertDialog.show();
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		ArtistList.this.loadArtists(result);
    	}
    }
}

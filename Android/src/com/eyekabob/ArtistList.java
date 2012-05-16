/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.eyekabob.models.Artist;
import com.eyekabob.util.JSONTask;

public class ArtistList extends EyekabobActivity {
	ArtistListAdapter adapter;
	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Artist artist = (Artist)parent.getAdapter().getItem(position);
			Intent intent = new Intent(getApplicationContext(), ArtistInfo.class);
			intent.putExtra("artist", artist);
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

    protected void loadArtists(JSONObject response) {
    	try {
	    	JSONObject results = response.getJSONObject("results");
	    	Object artistMatchesObj = results.get("artistmatches");
	    	if (artistMatchesObj instanceof String) {
	    		LinearLayout noResultsLayout = (LinearLayout)findViewById(R.id.noResults);
	    		noResultsLayout.setVisibility(View.VISIBLE);
	    		return;
	    	}
	    	JSONArray artists = ((JSONObject)artistMatchesObj).getJSONArray("artist");
	    	for (int i = 0; i < artists.length(); i++) {
	    		JSONObject artistNode = artists.getJSONObject(i);
	    		String name = artistNode.getString("name");
	    		String mbid = artistNode.getString("mbid");
	    		String url = artistNode.getString("url");

	    		String image = "";
	    		JSONArray images = artistNode.getJSONArray("image");
	    		for (int j = 0; j < images.length(); j++) {
	    			JSONObject imageJson = images.getJSONObject(j);
	    			if ("large".equals(imageJson.getString("size"))) {
	    				image = imageJson.getString("#text");
	    			}
	    		}

	    		Artist artist = new Artist();
	    		artist.setName(name);
	    		artist.setMbid(mbid);
	    		artist.setUrl(url);
	    		artist.addImageURL("large", image);
	    		adapter.add(artist);
	    	}
    	}
    	catch (JSONException e) {
    		Log.e(getClass().getName(),"", e);
    	}
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends JSONTask {
    	protected void onPreExecute() {
    		ArtistList.this.createDialog(R.string.searching);
    		ArtistList.this.showDialog();
    	}
    	protected void onPostExecute(JSONObject result) {
    		ArtistList.this.dismissDialog();
    		ArtistList.this.loadArtists(result);
    	}
    }

    public void addBand(View v) {
    	Intent addBandIntent = new Intent(this, AddBand.class);
    	startActivity(addBandIntent);
    }
}

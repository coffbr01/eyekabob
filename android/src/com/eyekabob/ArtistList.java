/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.eyekabob.adapters.ArtistListAdapter;
import com.eyekabob.models.Artist;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        lv.setOnItemClickListener(listItemListener);
        String artist = getIntent().getExtras().getString("artist");

        // Send last.fm request.
        Map<String, String> lastFMParams = new HashMap<String, String>();
        lastFMParams.put("artist", artist);
        Uri lastFMUri = EyekabobHelper.LastFM.getUri("artist.search", lastFMParams);
        new LastFMRequestTask().execute(lastFMUri.toString());
    }

    @Override
    public void onDestroy() {
        adapter.clearCache();
        super.onDestroy();
    }

    protected void showNoResultsLabel() {
        LinearLayout noResultsLayout = (LinearLayout)findViewById(R.id.noResults);
        noResultsLayout.setVisibility(View.VISIBLE);
    }

    protected void loadLastFMArtists(JSONObject response) {
        try {
            if (response == null || !response.has("results")) {
                showNoResultsLabel();
                return;
            }

            JSONObject results = response.getJSONObject("results");
            Object artistMatchesObj = results.get("artistmatches");

            if (artistMatchesObj instanceof String) {
                showNoResultsLabel();
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
                    if ("medium".equals(imageJson.getString("size"))) {
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
            Log.e(getClass().getName(), "JSON was not in the expected format", e);
        }
    }

    private class LastFMRequestTask extends JSONTask {
        protected void onPreExecute() {
            ArtistList.this.createDialog(R.string.searching);
            ArtistList.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            ArtistList.this.dismissDialog();
            ArtistList.this.loadLastFMArtists(result);
        }
    }
}

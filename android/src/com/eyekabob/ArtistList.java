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
import android.widget.ListView;
import android.widget.Toast;
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
        if (artist != null) {
            artist = artist.trim();
        }

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

    protected void loadLastFMArtists(JSONObject response) {
        try {
            if (response == null || !response.has("results")) {
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject results = response.getJSONObject("results");
            Object artistMatchesObj = results.get("artistmatches");

            if (artistMatchesObj instanceof String) {
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_LONG).show();
                return;
            }

            JSONArray artists = ((JSONObject)artistMatchesObj).getJSONArray("artist");
            for (int i = 0; i < artists.length(); i++) {
                JSONObject artistNode = artists.getJSONObject(i);
                String name = artistNode.getString("name");
                String mbid = artistNode.getString("mbid");
                String url = artistNode.getString("url");

                JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage("large", artistNode.getJSONArray("image"));

                Artist artist = new Artist();
                artist.setName(name);
                artist.setMbid(mbid);
                artist.setUrl(url);
                artist.addImageURL("large", jsonImage.getString("#text"));
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

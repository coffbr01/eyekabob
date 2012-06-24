/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.eyekabob.models.Artist;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

public class ArtistInfo extends EyekabobActivity implements View.OnClickListener {
    private Artist artist;
    private String imageUrl;
    private Button detailsButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_info);
        WebView summaryWebView = (WebView)findViewById(R.id.artistSummary);
        summaryWebView.setBackgroundColor(0x00000000);
        artist = (Artist)getIntent().getExtras().get("artist");
        Log.d(getClass().getName(), "Artist name is: " + artist.getName());
        findViewById(R.id.artistDetailsButton).setOnClickListener(this);
        detailsButton = (Button)findViewById(R.id.artistDetailsButton);
        detailsButton.setVisibility(View.GONE);

        Map<String, String> params = new HashMap<String, String>();
        if (artist.getMbid() == null) {
            params.put("artist", artist.getName());
        }
        else {
            params.put("mbid", artist.getMbid());
        }
        Uri artistInfoUri = EyekabobHelper.LastFM.getUri("artist.getInfo", params);

        // Send the request for artist info.
        new ArtistRequestTask().execute(artistInfoUri.toString());
    }

    /**
     * This method is called after the last.fm response is received. It will
     * parse the XML document response and put attributes on the Artist object.
     */
    protected void handleArtistResponse(JSONObject response) {
        try {
            JSONObject jsonArtist = response.optJSONObject("artist");
            if (jsonArtist == null) {
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
                return;
            }
            artist.setName(jsonArtist.getString("name"));
            artist.setMbid(jsonArtist.getString("mbid"));
            artist.setUrl(jsonArtist.getString("url"));
            JSONObject image = EyekabobHelper.LastFM.getJSONImage("large", jsonArtist.getJSONArray("image"));
            imageUrl = image.getString("#text");

            JSONObject bio = jsonArtist.getJSONObject("bio");
            artist.setSummary(bio.getString("summary"));
            artist.setContent(bio.getString("content"));
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }

        render();
    }

    /**
     * Puts attributes from the Artist object into the views.
     */
    private void render() {
        // TODO: Phil to put rendering code here.

        // Example:
        // ------------------------------------------------------------------------------------
        TextView headerTextView = (TextView)findViewById(R.id.artistHeader);
        headerTextView.setText(artist.getName());

        ImageView iv = (ImageView)findViewById(R.id.artistImageView);
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

        WebView summaryWebView = (WebView)findViewById(R.id.artistSummary);
        String summaryHtml = "<div style='color:white'>" + artist.getSummary() + "</div>";
        summaryWebView.loadData(summaryHtml, "text/html", "UTF8");

        if (!("").equals(artist.getContent())) {
            detailsButton.setVisibility(View.VISIBLE);
            detailsButton.setText("More Info...");
        }
        // ------------------------------------------------------------------------------------
    }

    // Handles the asynchronous request, away from the UI thread.
    private class ArtistRequestTask extends JSONTask {
        protected void onPreExecute() {
            ArtistInfo.this.createDialog(R.string.loading);
            ArtistInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            ArtistInfo.this.dismissDialog();
            ArtistInfo.this.handleArtistResponse(result);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.artistDetailsButton) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("artist", artist);
            Intent intent = new Intent();
            intent.setClass(ArtistInfo.this, ArtistDetailInfo.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

import static android.graphics.Bitmap.createScaledBitmap;

public class ArtistInfo extends EyekabobActivity {
    private Artist artist;
    private String imageUrl;
    private Button detailsButton;

    private View.OnClickListener linksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.findLiveMusicButton) {
                Intent findMusicIntent = new Intent(ArtistInfo.this, SearchIntermediate.class);
                startActivity(findMusicIntent);
            } else if (view.getId() == R.id.aboutButton) {
                // TODO: Implement ABOUT page.
                Toast.makeText(ArtistInfo.this, "We are awesome!", Toast.LENGTH_SHORT).show();
                return;
            } else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(ArtistInfo.this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_info);
        WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
        contentWebView.setBackgroundColor(0x00000000);
        artist = (Artist)getIntent().getExtras().get("artist");
        Log.d(getClass().getName(), "Artist name is: " + artist.getName());
        findViewById(R.id.findLiveMusicButton).setOnClickListener(linksListener);
        findViewById(R.id.aboutButton).setOnClickListener(linksListener);
        findViewById(R.id.contactButton).setOnClickListener(linksListener);
//        findViewById(R.id.artistDetailsButton).setOnClickListener(this);
//        detailsButton = (Button)findViewById(R.id.artistDetailsButton);
//        detailsButton.setVisibility(View.GONE);

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
        TextView artistNameView = (TextView)findViewById(R.id.infoMainHeader);
        artistNameView.setText(artist.getName());

        //TODO: Get actual concert date
        TextView nextConcertDateView = (TextView)findViewById(R.id.infoSubHeaderOne);
        nextConcertDateView.setText("Next Concert: December 13 @");

        //TODO: Get actual concert location
        TextView nextConcertLocationView = (TextView)findViewById(R.id.infoSubHeaderTwo);
        nextConcertLocationView.setText("Somewhere Badass!!");

        ImageView iv = (ImageView)findViewById(R.id.infoImageView);
        InputStream is = null;
        try {
            is = (InputStream) new URL(imageUrl).getContent();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        View topLine = (View)findViewById(R.id.infoDividerLineTop);
        topLine.setVisibility(View.VISIBLE);
        View bottomLine = (View)findViewById(R.id.infoDividerLineBottom);
        bottomLine.setVisibility(View.VISIBLE);

        // Get the image and re-size it to fit the screen
        Bitmap img = BitmapFactory.decodeStream(is);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int ratio = metrics.widthPixels / img.getWidth();
        Bitmap rescaledImg = createScaledBitmap(img, img.getWidth() * ratio, img.getHeight() * ratio, false);
        iv.setImageBitmap(rescaledImg);

        if (!artist.getSummary().equals("")) {
            TextView bioHeaderView = (TextView)findViewById(R.id.infoBioHeader);
            bioHeaderView.setText("Bio");
        }

        WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
        String contentHtml = "<div style='color:white'>" + artist.getContent() + "</div>";
        contentWebView.loadData(contentHtml, "text/html", "UTF8");

        // TODO: Something similar to Bio above
//        if (!artist.getFutureEvents().equals("")) {
//            TextView futureEventsHeaderView = (TextView)findViewById(R.id.infoFutureEventsHeader);
//            futureEventsHeaderView.setText("Future Events");
//        }
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
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import com.eyekabob.models.Event;
import com.eyekabob.models.Venue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.eyekabob.models.Artist;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

import static android.graphics.Bitmap.createScaledBitmap;

public class ArtistInfo extends EyekabobActivity {
    private Artist artist;
    private ArrayList<Event> futureEvents = new ArrayList<Event>(){};
    private String imageUrl;
    private boolean artistInfoReturned = false;
    private boolean futureEventsInfoReturned = false;

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
            } else if (view.getId() == R.id.infoBioToggleButton) {
                ToggleButton tb = (ToggleButton) findViewById(R.id.infoBioToggleButton);
                toggleBioText(tb.isChecked());
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
        findViewById(R.id.infoBioToggleButton).setOnClickListener(linksListener);

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

        // Send last.fm request.
        Map<String, String> lastFMParams = new HashMap<String, String>();
        lastFMParams.put("artist", artist.getName());
        Uri lastFMUri = EyekabobHelper.LastFM.getUri("artist.getEvents", lastFMParams);
        new FutureEventsRequestTask().execute(lastFMUri.toString());
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
                artistInfoReturned = true;
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

        artistInfoReturned = true;
        if (futureEventsInfoReturned) {
            render();
        }
    }

    /**
     * This method is called after the last.fm response is received. It will
     * parse the XML document response and put attributes on the Artist object.
     */
    protected void handleFutureEventsResponse(JSONObject response) {
        try {
            JSONObject jsonEvents = response.optJSONObject("events");
            if (jsonEvents == null) {
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
                futureEventsInfoReturned = true;
                return;
            }

            JSONArray jsonEventsArray = jsonEvents.getJSONArray("event");
            for (int i = 0; i < jsonEventsArray.length() && i < 10; i++) {
                Event event = new Event();
                Venue venue = new Venue();
                event.setVenue(venue);

                JSONObject jsonEvent = jsonEventsArray.getJSONObject(i);
                JSONObject jsonVenue = jsonEvent.getJSONObject("venue");
                JSONObject jsonLocation = jsonVenue.getJSONObject("location");
                JSONObject jsonGeo = jsonLocation.optJSONObject("geo:point");

                event.setId(jsonEvent.getString("id"));
                event.setName(jsonEvent.getString("title"));
                event.setDate(EyekabobHelper.LastFM.toReadableDate(jsonEvent.getString("startDate")));
                JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage("large", jsonEvent.getJSONArray("image"));
                event.addImageURL("large", jsonImage.getString("#text"));

                venue.setName(jsonVenue.getString("name"));
                venue.setCity(jsonLocation.getString("city"));

                if (jsonGeo != null) {
                    venue.setLat(jsonGeo.optString("geo:lat"));
                    venue.setLon(jsonGeo.getString("geo:long"));
                }
                
                futureEvents.add(event);
            }
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }

        futureEventsInfoReturned = true;
        if (artistInfoReturned)  {
            render();    
        }
    }

    /**
     * Puts attributes from the Artist object into the views.
     */
    private void render() {
        TextView artistNameView = (TextView)findViewById(R.id.infoMainHeader);
        artistNameView.setText(artist.getName());

        TextView nextConcertDateView = (TextView)findViewById(R.id.infoSubHeaderOne);
        TextView nextConcertLocationView = (TextView)findViewById(R.id.infoSubHeaderTwo);
        Event nextEvent;
        if (futureEvents.size() > 0) {
            nextEvent = futureEvents.get(0);
            nextConcertDateView.setText("Next Concert: " + nextEvent.getDate() + " @");
            nextConcertLocationView.setText(nextEvent.getVenue().getName() + " in " + nextEvent.getVenue().getCity());
        } else {
            nextConcertDateView.setText("Next Concert: UNKNOWN");
        }

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
        if (!artist.getContent().equals("")) {
            ToggleButton tb = (ToggleButton)findViewById(R.id.infoBioToggleButton);
            tb.setVisibility(View.VISIBLE);
        }

        WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
        String contentHtml = "<div style='color:white'>" + artist.getSummary() + "</div>";
        contentWebView.loadData(contentHtml, "text/html", "UTF8");

        if (futureEvents.size() > 0) {
            TextView futureEventsHeaderView = (TextView)findViewById(R.id.infoFutureEventsHeader);
            futureEventsHeaderView.setText("Future Events");
            String futureText = "";
            for (int i = 0; i < futureEvents.size(); i++) {
                Event event = futureEvents.get(i);
                futureText += event.getDate() + "\n";
                futureText += "@ " + event.getVenue().getName() + " in " + event.getVenue().getCity() + "\n\n";
            }
            TextView futureEventsContentView = (TextView)findViewById(R.id.infoFutureEventsContent);
            futureEventsContentView.setText(futureText);
        }
    }

    private void toggleBioText (boolean detailed) {
        if (detailed && !artist.getContent().equals("")) {
            WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
            String contentHtml = "<div style='color:white'>" + artist.getContent() + "</div>";
            contentWebView.loadData(contentHtml, "text/html", "UTF8");
        } else if (!detailed && !artist.getSummary().equals("")) {
            WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
            String contentHtml = "<div style='color:white'>" + artist.getSummary() + "</div>";
            contentWebView.loadData(contentHtml, "text/html", "UTF8");
        }
    }

    // Handles the asynchronous request, away from the UI thread.
    private class ArtistRequestTask extends JSONTask {
        protected void onPreExecute() {
//            ArtistInfo.this.createDialog(R.string.loading);
//            ArtistInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
//            ArtistInfo.this.dismissDialog();
            ArtistInfo.this.handleArtistResponse(result);
        }
    }

    // Handles the asynchronous request, away from the UI thread.
    private class FutureEventsRequestTask extends JSONTask {
        protected void onPreExecute() {
            ArtistInfo.this.createDialog(R.string.loading);
            ArtistInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            ArtistInfo.this.dismissDialog();
            ArtistInfo.this.handleFutureEventsResponse(result);
        }
    }
}

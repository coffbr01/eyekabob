/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.eyekabob.models.Artist;
import com.eyekabob.models.Event;
import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.ImageTask;
import com.eyekabob.util.JSONTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArtistInfo extends EyekabobActivity {
    private Artist artist;
    private ArrayList<Event> futureEvents = new ArrayList<Event>(){};
    private boolean artistInfoReturned = false;
    private boolean futureEventsInfoReturned = false;
    private boolean imageInfoReturned = false;

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.findLiveMusicButton) {
                Intent findMusicIntent = new Intent(ArtistInfo.this, SearchIntermediate.class);
                startActivity(findMusicIntent);
            }
            else if (view.getId() == R.id.aboutButton) {
                Dialog aboutDialog = EyekabobHelper.createAboutDialog(ArtistInfo.this);
                aboutDialog.show();
            }
            else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(ArtistInfo.this);
            }
            else if (view.getId() == R.id.infoBioToggleButton) {
                ToggleButton tb = (ToggleButton) findViewById(R.id.infoBioToggleButton);
                toggleBioText(tb.isChecked());
            }
            else {
                // Must be a future event
                FutureEventView row = (FutureEventView)view;
                Event event = row.getEvent();
                Intent intent = new Intent(getApplicationContext(), EventInfo.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_info);
        artist = (Artist)getIntent().getExtras().get("artist");
        Log.d(getClass().getName(), "Artist name is: " + artist.getName());
        findViewById(R.id.findLiveMusicButton).setOnClickListener(onClickListener);
        findViewById(R.id.aboutButton).setOnClickListener(onClickListener);
        findViewById(R.id.contactButton).setOnClickListener(onClickListener);
        findViewById(R.id.infoBioToggleButton).setOnClickListener(onClickListener);

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
        lastFMParams.put("limit", "10");
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
                dismissDialogIfReady();
                return;
            }
            artist.setName(jsonArtist.getString("name"));
            artist.setMbid(jsonArtist.getString("mbid"));
            artist.setUrl(jsonArtist.getString("url"));
            JSONObject jsonImage = EyekabobHelper.LastFM.getLargestJSONImage(jsonArtist.getJSONArray("image"));

            // Get artist image, if one exists.
            if (null != jsonImage) {
                new ArtistImageTask().execute(new URL(jsonImage.getString("#text")));
            } else {
                imageInfoReturned = true;
                dismissDialogIfReady();
            }

            JSONObject bio = jsonArtist.getJSONObject("bio");
            artist.setSummary(bio.getString("summary"));
            artist.setContent(bio.getString("content"));
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }
        catch (MalformedURLException e) {
            Log.e(getClass().getName(), "", e);
        }

        TextView artistNameView = (TextView)findViewById(R.id.infoMainHeader);
        artistNameView.setText(artist.getName());

        if (!(artist.getSummary() == null) && !artist.getSummary().equals("")) {
            TextView bioHeaderView = (TextView)findViewById(R.id.infoBioHeader);
            // TODO: I18N
            bioHeaderView.setText("Bio");
            TextView bioView = (TextView)findViewById(R.id.infoBioContent);
            String contentHtml = artist.getSummary();
            bioView.setText(Html.fromHtml(contentHtml));
            bioView.setVisibility(View.VISIBLE);
        }
        if (!(artist.getContent() == null) && !artist.getContent().equals("")) {
            ToggleButton tb = (ToggleButton)findViewById(R.id.infoBioToggleButton);
            tb.setVisibility(View.VISIBLE);
        }

        artistInfoReturned = true;
        dismissDialogIfReady();
    }

    /**
     * This method is called after the last.fm response is received. It will
     * parse the JSON response and put attributes on the Artist object.
     */
    protected void handleFutureEventsResponse(JSONObject response) {
        try {
            JSONObject jsonEvents = response.optJSONObject("events");
            if (jsonEvents == null || !jsonEvents.has("event")) {
                futureEventsInfoReturned = true;
                dismissDialogIfReady();
                return;
            }

            JSONArray jsonEventsArray = jsonEvents.getJSONArray("event");
            for (int i = 0; i < jsonEventsArray.length(); i++) {
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

        TextView nextConcertDateView = (TextView)findViewById(R.id.infoSubHeaderOne);
        TextView nextConcertLocationView = (TextView)findViewById(R.id.infoSubHeaderTwo);
        Event nextEvent;
        if (futureEvents.size() > 0) {
            nextEvent = futureEvents.get(0);
            // TODO: I18N
            nextConcertDateView.setText("Next Concert: " + nextEvent.getDate() + " @");
            nextConcertLocationView.setText(nextEvent.getVenue().getName() + " in " + nextEvent.getVenue().getCity());
            TextView futureEventsHeaderView = (TextView)findViewById(R.id.infoFutureEventsHeader);
            // TODO: I18N
            futureEventsHeaderView.setText("Future Events");
            LinearLayout futureEventsContentView = (LinearLayout)findViewById(R.id.infoFutureEventsContent);
            int i;
            for (i = 0; i < futureEvents.size(); i++) {
                Event event = futureEvents.get(i);
                String futureText = event.getDate() + "\n";
                // TODO: I18N
                futureText += "@ " + event.getVenue().getName() + " in " + event.getVenue().getCity();
                FutureEventView row = new FutureEventView(this);
                row.setTextColor(Color.WHITE);
                row.setPadding(0, 0, 0, 20); // Bottom padding
                row.setText(futureText);
                row.setEvent(event);
                row.setOnClickListener(onClickListener);
                futureEventsContentView.addView(row);
            }
        } else {
            // TODO: I18N
            nextConcertDateView.setText("Next Concert: UNKNOWN");
        }

        futureEventsInfoReturned = true;
        dismissDialogIfReady();
    }

    // TODO: this is identical to the handleImageResponse in EventInfo,
    // and should be refactored.
    private void handleImageResponse(Bitmap img) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float metWidth = metrics.widthPixels;
        float imgWidth = img.getWidth();
        float ratio = metWidth / imgWidth;
        // Add a little buffer room
        int newWidth = (int) Math.floor(img.getWidth() * ratio) - 50;
        int newHeight = (int) Math.floor(img.getHeight() * ratio) - 50;

        ImageView iv = (ImageView)findViewById(R.id.infoImageView);
        Bitmap rescaledImg = Bitmap.createScaledBitmap(img, newWidth, newHeight, false);
        iv.setImageBitmap(rescaledImg);
        imageInfoReturned = true;
        dismissDialogIfReady();
    }

    private void dismissDialogIfReady() {
        if (imageInfoReturned && artistInfoReturned && futureEventsInfoReturned) {
            dismissDialog();
        }
    }

    private void toggleBioText (boolean detailed) {
        TextView bioView = (TextView)findViewById(R.id.infoBioContent);

        String contentHtml = "";
        if (artist != null && !"".equals(artist.getContent())) {
            if (detailed) {
                contentHtml = artist.getContent();
            }
            else {
                contentHtml = artist.getSummary();
            }
        }

        bioView.setText(Html.fromHtml(contentHtml));
    }

    // Handles the asynchronous request, away from the UI thread.
    private class ArtistRequestTask extends JSONTask {
        protected void onPreExecute() {
            ArtistInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            ArtistInfo.this.handleArtistResponse(result);
        }
    }

    private class FutureEventView extends TextView {
        private Event event;
        public FutureEventView(Context context) {
            super(context);
        }
        public void setEvent(Event event) {
            this.event = event;
        }
        public Event getEvent() {
            return event;
        }
    }

    // Handles the asynchronous request, away from the UI thread.
    private class FutureEventsRequestTask extends JSONTask {
        protected void onPostExecute(JSONObject result) {
            ArtistInfo.this.handleFutureEventsResponse(result);
        }
    }

    private class ArtistImageTask extends ImageTask {
        protected void onPostExecute(Bitmap img) {
            ArtistInfo.this.handleImageResponse(img);
        }
    }
}

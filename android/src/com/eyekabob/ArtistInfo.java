/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
    private ImgSize largestImgSize = ImgSize.MEGA;
    private JSONArray imgArray;

    private enum ImgSize {
        MEGA,
        X_LARGE,
        LARGE,
        MEDIUM,
        SMALL
    }
    private View.OnClickListener linksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.findLiveMusicButton) {
                Intent findMusicIntent = new Intent(ArtistInfo.this, SearchIntermediate.class);
                startActivity(findMusicIntent);
            }
            else if (view.getId() == R.id.aboutButton) {
                // TODO: Implement ABOUT page.
                Toast.makeText(ArtistInfo.this, "We are awesome!", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(ArtistInfo.this);
            }
            else if (view.getId() == R.id.infoBioToggleButton) {
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
                return;
            }
            artist.setName(jsonArtist.getString("name"));
            artist.setMbid(jsonArtist.getString("mbid"));
            artist.setUrl(jsonArtist.getString("url"));
            imgArray = jsonArtist.getJSONArray("image");
            JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage("mega", imgArray);

            // Get artist image.
            new ArtistImageTask().execute(new URL(jsonImage.getString("#text")));

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

        if (!artist.getSummary().equals("")) {
            TextView bioHeaderView = (TextView)findViewById(R.id.infoBioHeader);
            // TODO: I18N
            bioHeaderView.setText("Bio");
        }
        if (!artist.getContent().equals("")) {
            ToggleButton tb = (ToggleButton)findViewById(R.id.infoBioToggleButton);
            tb.setVisibility(View.VISIBLE);
        }

        WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);
        String contentHtml = artist.getSummary();
        contentWebView.loadData(contentHtml, "text/html", "UTF8");

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
                Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
                futureEventsInfoReturned = true;
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
            String futureText = "";
            int i;
            // TODO: events should be a list with an adapter, not a newline rendered text blob.
            for (i = 0; i < futureEvents.size(); i++) {
                Event event = futureEvents.get(i);
                futureText += event.getDate() + "\n";
                // TODO: I18N
                futureText += "@ " + event.getVenue().getName() + " in " + event.getVenue().getCity() + "\n\n";
            }
            TextView futureEventsContentView = (TextView)findViewById(R.id.infoFutureEventsContent);
            futureEventsContentView.setText(futureText);
        } else {
            // TODO: I18N
            nextConcertDateView.setText("Next Concert: UNKNOWN");
        }

        futureEventsInfoReturned = true;
        dismissDialogIfReady();
    }

    private void handleImageResponse(Bitmap img) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int ratio = metrics.widthPixels / img.getWidth();
        if ((img.getWidth() * ratio) <= 0 || (img.getHeight() * ratio <= 0)) {
            String imageText;
            switch (largestImgSize) {
                case MEGA:
                    imageText = "extralarge";
                    largestImgSize = ImgSize.X_LARGE;
                    break;
                case X_LARGE:
                    imageText = "large";
                    largestImgSize = ImgSize.LARGE;
                    break;
                case LARGE:
                    imageText = "medium";
                    largestImgSize = ImgSize.MEDIUM;
                    break;
                default:
                    imageText = "small";
                    largestImgSize = ImgSize.SMALL;
                    break;
            }
            try {
                JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage(imageText, imgArray);
                // Get artist image.
                new ArtistImageTask().execute(new URL(jsonImage.getString("#text")));
            }
            catch (JSONException e) {
                Log.e(getClass().getName(), "", e);
            }
            catch (MalformedURLException e) {
                Log.e(getClass().getName(), "", e);
            }
        } else {
            View bottomLine = findViewById(R.id.infoDividerLineBottom);
            bottomLine.setVisibility(View.VISIBLE);

            // Get the image and re-size it to fit the screen
            ImageView iv = (ImageView)findViewById(R.id.infoImageView);
            Bitmap rescaledImg = Bitmap.createScaledBitmap(img, img.getWidth() * ratio, img.getHeight() * ratio, false);
            iv.setImageBitmap(rescaledImg);
            imageInfoReturned = true;
            dismissDialogIfReady();
        }
    }

    private void dismissDialogIfReady() {
        if (imageInfoReturned && artistInfoReturned && futureEventsInfoReturned) {
            dismissDialog();
        }
    }

    private void toggleBioText (boolean detailed) {
        WebView contentWebView = (WebView)findViewById(R.id.infoBioContent);

        String contentHtml = "";
        if (artist != null && !"".equals(artist.getContent())) {
            if (detailed) {
                contentHtml = artist.getContent();
            }
            else {
                contentHtml = artist.getSummary();
            }
        }

        contentWebView.loadData(contentHtml, "text/html", "UTF8");
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

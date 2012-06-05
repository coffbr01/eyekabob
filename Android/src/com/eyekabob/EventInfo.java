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
import java.util.List;
import java.util.Map;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyekabob.models.Event;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;

public class EventInfo extends EyekabobActivity {
    private List<String> artists;
    private String startDate = "";
    private String headliner = "";
    private String imageUrl = "";
    private String title = "";
    private String venue = "";
    private String venueCity = "";
    private String venueStreet = "";
    private String venueUrl = "";
    private String description = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_info);
        Event event = (Event)getIntent().getExtras().get("event");
        Map<String, String> params = new HashMap<String, String>();
        params.put("event", event.getId());
        Uri uri = EyekabobHelper.LastFM.getUri("event.getInfo", params);
        new RequestTask().execute(uri.toString());

        WebView wv = (WebView)findViewById(R.id.eventDescription);
        wv.setBackgroundColor(0x00000000);
    }

    protected void loadEvent(JSONObject response) {
        try {
            JSONObject jsonEvent = response.getJSONObject("event");

            artists = new ArrayList<String>();
            title = jsonEvent.getString("title");
            JSONObject jsonAllArtists = jsonEvent.getJSONObject("artists");
            headliner = jsonAllArtists.getString("headliner");
            JSONArray jsonOpeners = jsonAllArtists.getJSONArray("artist");
            for (int i = 0; i < jsonOpeners.length(); i++) {
                String artistName = jsonOpeners.getString(i);
                if (!headliner.equals(artistName)) {
                    artists.add(artistName);
                }
            }

            JSONObject jsonVenue = jsonEvent.getJSONObject("venue");
            venue = jsonVenue.optString("name");
            venueCity = jsonVenue.optString("city");
            venueStreet = jsonVenue.optString("street");
            venueUrl = jsonVenue.optString("url");
            startDate = EyekabobHelper.LastFM.toReadableDate(jsonEvent.getString("startDate"));
            JSONObject image = EyekabobHelper.LastFM.getJSONImage("large", jsonEvent.getJSONArray("image"));
            imageUrl = image.getString("#text");
            description = jsonEvent.getString("description");
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }

        ImageView iv = (ImageView)findViewById(R.id.eventImageView);
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

        TextView titleView = (TextView)findViewById(R.id.eventTitle);
        titleView.setText(title);

        TextView headlinerView = (TextView)findViewById(R.id.eventHeadliner);
        headlinerView.setText(headliner);

        String openers = "";
        TextView artistsView = (TextView)findViewById(R.id.eventArtists);
        if (!artists.isEmpty()) {
            openers += "\nAlso Performing:";
            for (String artist : artists) {
                openers += "\n        " + artist;
            }
        }
        artistsView.setText(openers);

        String venueDesc = "";
        TextView venueView = (TextView)findViewById(R.id.eventVenue);
        venueDesc += "         " + venue;
        if (!venueCity.equals("") && !venueStreet.equals("")) {
            venueDesc += "\n         Address: " + venueStreet + "\n" + venueCity;
        }
        venueDesc += "\n         " + startDate;

        TextView venueTitleView = (TextView)findViewById(R.id.venue_title);
        if (!venue.equals("") || !venueCity.equals("") || !venueStreet.equals("")) {
            venueTitleView.setText("Venue Details:");
        } else {
            venueTitleView.setText("No Venue details at this time");
        }
        venueView.setText(venueDesc);

        TextView websiteView = (TextView)findViewById(R.id.eventVenueWebsite);
        if (!venueUrl.equals("")) {
            websiteView.setText(Html.fromHtml("<a href=\""
                    + venueUrl + "\">More Information</a>"));
            websiteView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (!description.equals("")) {
            TextView descriptionTitleView = (TextView)findViewById(R.id.description_title);
            descriptionTitleView.setText("Description:");
        }
        WebView wv = (WebView)findViewById(R.id.eventDescription);
        description = "<div style='color:white'>" + description + "</div>";
        wv.loadData(description, "text/html", "UTF8");
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends JSONTask {
        protected void onPreExecute() {
            EventInfo.this.createDialog(R.string.loading);
            EventInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            EventInfo.this.dismissDialog();
            EventInfo.this.loadEvent(result);
        }

    }
}

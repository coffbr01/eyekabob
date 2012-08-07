/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.eyekabob.models.Event;
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
import java.util.List;
import java.util.Map;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_info);
        findViewById(R.id.findLiveMusicButton).setOnClickListener(linksListener);
        findViewById(R.id.aboutButton).setOnClickListener(linksListener);
        findViewById(R.id.contactButton).setOnClickListener(linksListener);
        findViewById(R.id.infoTicketsButton).setOnClickListener(linksListener);
        Event event = (Event)getIntent().getExtras().get("event");
        Map<String, String> params = new HashMap<String, String>();
        params.put("event", event.getId());
        Uri uri = EyekabobHelper.LastFM.getUri("event.getInfo", params);
        new RequestTask().execute(uri.toString());
    }

    private View.OnClickListener linksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.findLiveMusicButton) {
                Intent findMusicIntent = new Intent(EventInfo.this, SearchIntermediate.class);
                startActivity(findMusicIntent);
            }
            else if (view.getId() == R.id.aboutButton) {
                Dialog aboutDialog = EyekabobHelper.createAboutDialog(EventInfo.this);
                aboutDialog.show();
            }
            else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(EventInfo.this);
            }
            else if (view.getId() == R.id.infoTicketsButton) {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tm_link", "tm_header_search");
                params.put("user_input", headliner);
                params.put("q", headliner);
                params.put("search.x", "0");
                params.put("search.y", "0");
                Intent tixIntent = new Intent(Intent.ACTION_VIEW, EyekabobHelper.TicketMaster.getURI(params));
                startActivity(tixIntent);
            }
        }
    };

    protected void loadEvent(JSONObject response) {
        try {
            JSONObject jsonEvent = response.getJSONObject("event");

            artists = new ArrayList<String>();
            title = jsonEvent.getString("title");
            JSONObject jsonAllArtists = jsonEvent.getJSONObject("artists");
            headliner = jsonAllArtists.getString("headliner");
            Object artistObj = jsonAllArtists.get("artist");
            JSONArray jsonOpeners = new JSONArray();
            if (artistObj instanceof JSONArray) {
                jsonOpeners = (JSONArray)artistObj;
            }
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
            JSONObject image = EyekabobHelper.LastFM.getLargestJSONImage(jsonEvent.getJSONArray("image"));
            imageUrl = image.getString("#text");
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }

        try {
            new EventImageTask().execute(new URL(imageUrl));
        }
        catch (MalformedURLException e) {
            Log.e(getClass().getName(), "Bad image URL [" + imageUrl + "]", e);
        }

        TextView titleView = (TextView)findViewById(R.id.infoMainHeader);
        titleView.setText(title);

        TextView headlinerView = (TextView)findViewById(R.id.infoSubHeaderOne);
        // TODO: I18N
        headlinerView.setText("Headlining: " + headliner);

        TextView dateTimeView = (TextView)findViewById(R.id.infoSubHeaderTwo);
        dateTimeView.setText(startDate);

        if (!startDate.equals("")) {
            Button tixButton = (Button)findViewById(R.id.infoTicketsButton);
            tixButton.setVisibility(View.VISIBLE);
        }

        LinearLayout artistsView = (LinearLayout)findViewById(R.id.infoFutureEventsContent);
        TextView alsoPerformingView = (TextView)findViewById(R.id.infoFutureEventsHeader);
        if (!artists.isEmpty()) {
            // TODO: I18N
            alsoPerformingView.setText("Also Performing:");
            for (String artist : artists) {
                TextView row = new TextView(this);
                row.setTextColor(Color.WHITE);
                row.setText(artist);
                row.setPadding(20, 0, 0, 20); // Left and bottom padding
                artistsView.addView(row);
            }
        }

        String venueDesc = "";
        TextView venueView = (TextView)findViewById(R.id.infoEventVenue);
        // TODO: Padding instead of whitespace
        venueDesc += "         " + venue;
        if (!venueCity.equals("") && !venueStreet.equals("")) {
            // TODO: I18N
            venueDesc += "\n         Address: " + venueStreet + "\n" + venueCity;
        }
        // TODO: Padding instead of whitespace
        venueDesc += "\n         " + startDate;

        TextView venueTitleView = (TextView)findViewById(R.id.infoBioHeader);
        if (!venue.equals("") || !venueCity.equals("") || !venueStreet.equals("")) {
            // TODO: I18N
            venueTitleView.setText("Venue Details:");
            View vView = findViewById(R.id.infoVenueDetails);
            vView.setVisibility(View.VISIBLE);
        } else {
            // TODO: I18N
            venueTitleView.setText("No Venue Details Available");
        }

        venueView.setVisibility(View.VISIBLE);
        venueView.setText(venueDesc);

        TextView websiteView = (TextView)findViewById(R.id.infoVenueWebsite);
        if (!venueUrl.equals("")) {
            // TODO: I18N
            websiteView.setVisibility(View.VISIBLE);
            websiteView.setText(Html.fromHtml("<a href=\"" + venueUrl + "\">More Information</a>"));
            websiteView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    // TODO: this is identical to the handleImageResponse in ArtistInfo,
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
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends JSONTask {
        protected void onPreExecute() {
            EventInfo.this.createDialog(R.string.loading);
            EventInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            EventInfo.this.loadEvent(result);
        }
    }

    private class EventImageTask extends ImageTask {
        protected void onPostExecute(Bitmap img) {
            EventInfo.this.handleImageResponse(img);
            EventInfo.this.dismissDialog();
        }
    }
}

/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.eyekabob.adapters.EventListAdapter;
import com.eyekabob.models.Event;
import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.JSONTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VenueInfo extends EyekabobActivity {
    private EventListAdapter eventsAdapter;

    private View.OnClickListener linksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.findLiveMusicButton) {
                Intent findMusicIntent = new Intent(VenueInfo.this, SearchIntermediate.class);
                startActivity(findMusicIntent);
            }
            else if (view.getId() == R.id.aboutButton) {
                Dialog aboutDialog = EyekabobHelper.createAboutDialog(VenueInfo.this);
                aboutDialog.show();
            }
            else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(VenueInfo.this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_info);
        findViewById(R.id.findLiveMusicButton).setOnClickListener(linksListener);
        findViewById(R.id.aboutButton).setOnClickListener(linksListener);
        findViewById(R.id.contactButton).setOnClickListener(linksListener);
        Venue thisVenue = (Venue) getIntent().getExtras().get("venue");
        Map<String, String> params = new HashMap<String, String>();
        params.put("venue", thisVenue.getId());

        TextView nameView = (TextView)findViewById(R.id.venueNameView);
        nameView.setText(thisVenue.getName());

        TextView infoView = (TextView)findViewById(R.id.venueInfoView);
        nameView.setText(thisVenue.getName());
        String venueDesc = "";
        // TODO: Padding instead of whitespace
        if (!thisVenue.getCity().equals("") && !thisVenue.getStreet().equals("")) {
            // TODO: I18N
            venueDesc += "Address:\n  " + thisVenue.getStreet() + "\n  " + thisVenue.getCity();
        }
        infoView.setText(venueDesc);

        TextView websiteView = (TextView)findViewById(R.id.venueMoreInfoView);
        if (!"".equals((thisVenue.getUrl().toString()))) {
            // TODO: I18N
            websiteView.setText(Html.fromHtml("<a href=\"" + thisVenue.getUrl() + "\">More Information</a>"));
            websiteView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        Uri uri = EyekabobHelper.LastFM.getUri("venue.getEvents", params);
        new RequestTask().execute(uri.toString());
    }

    private AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Event event = (Event)parent.getAdapter().getItem(position);
            Intent intent = new Intent(getApplicationContext(), EventInfo.class);
            intent.putExtra("event", event);
            startActivity(intent);
        }
    };

    protected void loadEvent(JSONObject response) {
        try {
            JSONObject jsonResponse = response.getJSONObject("events");
            JSONArray jsonEvents = jsonResponse.getJSONArray("event");
            if (jsonEvents.length() > 0) {
                findViewById(R.id.venueInfoUpcomingHeader).setVisibility(View.VISIBLE);
                findViewById(R.id.venueInfoUpcomingEvents).setVisibility(View.VISIBLE);
            }
            eventsAdapter = new EventListAdapter(getApplicationContext());
            for (int i = 0; i < jsonEvents.length() && i < 10; i++) {
                JSONObject jsonEvent = jsonEvents.getJSONObject(i);
                Event event = new Event();
                event.setId(jsonEvent.getString("id"));
                event.setName(jsonEvent.getString("title"));
                event.setDate(EyekabobHelper.LastFM.toReadableDate(jsonEvent.getString("startDate")));
                JSONObject jsonImage = EyekabobHelper.LastFM.getJSONImage("large", jsonEvent.getJSONArray("image"));
                event.addImageURL("large", jsonImage.getString("#text"));

                eventsAdapter.add(event);
            }
        } catch (JSONException e) {
            Log.e(getClass().getName(), "", e);
        }

        ListView eventsView = (ListView)findViewById(R.id.venueInfoUpcomingEvents);
        eventsView.setAdapter(eventsAdapter);
        eventsView.setOnItemClickListener(listItemListener);
    }

    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends JSONTask {
        protected void onPreExecute() {
            VenueInfo.this.createDialog(R.string.loading);
            VenueInfo.this.showDialog();
        }
        protected void onPostExecute(JSONObject result) {
            VenueInfo.this.dismissDialog();
            VenueInfo.this.loadEvent(result);
        }
    }
}

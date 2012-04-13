package com.eyekabob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;

public class CheckinSearchList extends EyekabobActivity {

	private OnItemClickListener listItemListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Venue venue = (Venue)parent.getAdapter().getItem(position);
			Bundle params = new Bundle();
			params.putString("place", venue.getId().toString());
			try {
				// TODO: this doesn't work yet!
				EyekabobHelper.Facebook.getInstance().request("me/checkins", params, "POST");
			} catch (Exception e) {
				Log.e(CheckinSearchList.this.getClass().getName(), "Problem checking in to [" + venue.getName() + "]", e);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adlistactivity);
		VenueListAdapter adapter = new VenueListAdapter(this);
		ListView lv = (ListView)findViewById(R.id.adList);
		lv.setOnItemClickListener(listItemListener);
		lv.setAdapter(adapter);
		Location location = (Location)getIntent().getExtras().get("location");

    	Bundle params = new Bundle();
		params.putCharSequence("center", location.getLatitude() + "," + location.getLongitude());
    	params.putCharSequence("type", "place");
    	params.putCharSequence("distance", "50000"); // 50km, max supported by Facebook.
    	new JSONRequestTask().execute(params);
	}

	public void loadData(JSONObject data) {
		try {
			// The key for the list of places is "data".
			JSONArray list = data.getJSONArray("data");
			ListView lv = (ListView)findViewById(R.id.adList);
			VenueListAdapter adapter = (VenueListAdapter)lv.getAdapter();
			for (int i = 0; i < list.length(); i++) {
				JSONObject placeData = list.getJSONObject(i);
				Venue venue = new Venue();
				venue.setName(placeData.optString("name"));
				venue.setId(placeData.optInt("id"));
				JSONObject location = placeData.optJSONObject("location");
				if (location != null) {
					// TODO: enable this after you can sort by distance!
					//venue.setLat(location.optString("latitude"));
					//venue.setLon(location.optString("longitude"));
					venue.setCity(location.optString("city"));
					venue.setStreet(location.optString("street"));
					venue.setPostalCode(location.optString("zip"));
					venue.setCountry(location.optString("country"));
				}
				adapter.add(venue);
			}
		}
		catch (JSONException e) {
			Toast.makeText(this, "Error in reading Facebook place search", Toast.LENGTH_SHORT).show();
			Log.e(getClass().getName(), "Unable to parse JSON response", e);
		}
	}

	public class JSONRequestTask extends AsyncTask<Bundle, Void, JSONObject> {
    	protected void onPreExecute() {
    		CheckinSearchList.this.createDialog(R.string.searching);
    		CheckinSearchList.this.showDialog();
    	}
		@Override
		protected JSONObject doInBackground(Bundle... args) {
			Bundle params = args[0];
	    	JSONObject response = null;
	    	try {
				String jsonString = EyekabobHelper.Facebook.getInstance().request("search", params);
				response = new JSONObject(jsonString);
			} catch (Exception e) {
				Log.e(this.getClass().getName(),"Facebook place search error", e);
			}
	    	return response;
		}
    	protected void onPostExecute(JSONObject result) {
    		CheckinSearchList.this.dismissDialog();

	    	if (result == null) {
	    		Toast.makeText(CheckinSearchList.this, "Error during facebook place search.", Toast.LENGTH_SHORT).show();
	    		return;
	    	}

	    	CheckinSearchList.this.loadData(result);
    	}
	}
}

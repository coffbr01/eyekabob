package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

public class EyekabobHelper {
	public static final Map<String, String> zipToNameMap = new HashMap<String, String>();

	public static Location getLocation(Activity activity) {
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		// Gets the last known location from the best service.
		String bestLocationProvider = locationManager.getBestProvider(new Criteria(), true /*enabled only*/);
		return locationManager.getLastKnownLocation(bestLocationProvider);
	}

	public static class LastFM {
		public static final String USER = "eyekabob";
		public static final String PASS = "eyekabob";
		public static final String API_KEY = "d9cd2f150cf10b139ab481e462272f3f";
		public static final String SECRET = "3a0bdf658071649c7a2594aa63ec1062";
		public static final String AUTH_TOKEN = "167550e2be8e2989ab56e63b03dd1db6";
		public static final String SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

		public static Uri getUri(String method, Map<String, String> params) {
			if (params == null) {
				params = new HashMap<String, String>();
			}

			if (!params.containsKey("limit")) {
				params.put("limit", "100");
			}

			params.put("api_key", API_KEY);

			String url = SERVICE_URL;
			url += "?method=" + method;
			for (String param : params.keySet()) {
				url += "&" + param + "=" + params.get(param);
			}
			return Uri.parse(url);
		}
	}
	public static class GeoNames {
		public static final String SERVICE_URL = "http://api.geonames.org/postalCodeLookupJSON";
		public static Uri getUri(String zip) {
			String url = SERVICE_URL + "?username=eyekabob&country=US&postalcode=" + zip;
			return Uri.parse(url);
		}
	}
}

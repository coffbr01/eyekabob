package com.eyekabob;

import java.util.Map;

import android.net.Uri;

public class EyekabobHelper {
	public static class LastFM {
		public static final String USER = "eyekabob";
		public static final String PASS = "eyekabob";
		public static final String API_KEY = "d9cd2f150cf10b139ab481e462272f3f";
		public static final String SECRET = "3a0bdf658071649c7a2594aa63ec1062";
		public static final String AUTH_TOKEN = "167550e2be8e2989ab56e63b03dd1db6";
		public static final String SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

		public static Uri getUri(String method, Map<String, String> params) {
			String url = SERVICE_URL;
			url += "?method=" + method;
			url += "&api_key=" + API_KEY;
			if (params != null) {
				for (String param : params.keySet()) {
					url += "&" + param + "=" + params.get(param);
				}
			}
			return Uri.parse(url);
		}

	}
}

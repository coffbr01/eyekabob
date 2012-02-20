package com.eyekabob;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class EyekabobHelper {
	public static class LastFM {
		public static final String USER = "eyekabob";
		public static final String PASS = "eyekabob";
		public static final String API_KEY = "d9cd2f150cf10b139ab481e462272f3f";
		public static final String SECRET = "3a0bdf658071649c7a2594aa63ec1062";
		public static final String AUTH_TOKEN = "167550e2be8e2989ab56e63b03dd1db6";
		public static final String SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

		public static void makeRequest(Activity activity, String method, Map<String, String> params) {
			String url = SERVICE_URL;
			url += "?method=" + method;
			url += "&api_key=" + API_KEY;
			for (String param : params.keySet()) {
				url += "&" + param + "=" + params.get(param);
			}

			new EyekabobHelper.RequestTask(activity).execute(url);
		}

	}

    // Handles the asynchronous request, away from the UI thread.
    public static class RequestTask extends AsyncTask<String, Void, String> {
    	private Context context;
    	private Dialog alertDialog;
    	public RequestTask(Activity activity) {
    		this.context = activity;
    	    Builder builder = new AlertDialog.Builder(context);
    	    builder.setMessage(R.string.searching);
    	    builder.setCancelable(false);
    	    alertDialog = builder.create();
    	    alertDialog.setOwnerActivity(activity);
    	}
    	protected void onPreExecute() {
    		alertDialog.show();
    	}
    	protected String doInBackground(String... urls) {
    		return doRequest(urls[0]);
    	}
    	protected void onPostExecute(String result) {
    		alertDialog.dismiss();
    		Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    	}
        public String doRequest(String urlStr) {
        	HttpURLConnection urlConnection = null;
        	StringBuffer out = null;
    		try {
    			URL url = new URL(urlStr);
    			urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                out = new StringBuffer();
                byte[] b = new byte[4096];
                for (int n; (n = is.read(b)) != -1;) {
                    out.append(new String(b, 0, n));
                }
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
            finally {
                urlConnection.disconnect();
            }

            return out.toString();
        }
    }
}

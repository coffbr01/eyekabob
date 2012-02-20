package com.eyekabob;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;

public class EyekabobHelper {
	public static class LastFM {
		public static final String USER = "eyekabob";
		public static final String PASS = "eyekabob";
		public static final String API_KEY = "d9cd2f150cf10b139ab481e462272f3f";
		public static final String SECRET = "3a0bdf658071649c7a2594aa63ec1062";
		public static final String AUTH_TOKEN = "167550e2be8e2989ab56e63b03dd1db6";
		public static final String SERVICE_URL = "http://ws.audioscrobbler.com/2.0/";

		public static void makeRequest(Activity activity, Class<?> nextView, String method, Map<String, String> params) {
			String url = SERVICE_URL;
			url += "?method=" + method;
			url += "&api_key=" + API_KEY;
			for (String param : params.keySet()) {
				url += "&" + param + "=" + params.get(param);
			}

			new EyekabobHelper.RequestTask(activity, nextView).execute(url);
		}

	}

    // Handles the asynchronous request, away from the UI thread.
    public static class RequestTask extends AsyncTask<String, Void, Document> {
    	private Activity activity;
    	private Dialog alertDialog;
    	private Class<?> nextView;
    	public RequestTask(Activity activity, Class<?> nextView) {
    		this.activity = activity;
    		this.nextView = nextView;
    	    Builder builder = new AlertDialog.Builder(activity);
    	    builder.setMessage(R.string.searching);
    	    builder.setCancelable(false);
    	    alertDialog = builder.create();
    	    alertDialog.setOwnerActivity(activity);
    	}
    	protected void onPreExecute() {
    		alertDialog.show();
    	}
    	protected Document doInBackground(String... urls) {
    		return doRequest(urls[0]);
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		Intent intent = new Intent(activity, nextView);
    		activity.startActivity(intent);
    	}
        public Document doRequest(String urlStr) {
        	Document result = null;
    		try {
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			result = builder.parse(urlStr);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		catch (ParserConfigurationException e) {
    			e.printStackTrace();
    		}
    		catch (SAXException e) {
    			e.printStackTrace();
    		}

            return result;
        }
    }
}

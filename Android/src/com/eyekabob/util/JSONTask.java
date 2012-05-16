/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public abstract class JSONTask extends AsyncTask<String, Void, JSONObject> {
	private String requestType;
	public JSONTask() {
		super();
	}
	public JSONTask(String requestType) {
		super();
		this.requestType = requestType;
	}
	protected JSONObject doInBackground(String... uris) {
		return doRequest(uris[0]);
	}
    public JSONObject doRequest(String uri) {
    	HttpClient client = new DefaultHttpClient();
    	HttpRequestBase request = null;
    	if ("POST".equals(requestType)) {
    		request = new HttpPost(uri);
    	}
    	else {
    		request = new HttpGet(uri);
    	}
    	StringBuffer sb = null;

    	try {
	    	HttpResponse response = client.execute(request);
	    	BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        sb = new StringBuffer();
	        String line = null;
	        while ((line = in.readLine()) != null) {
	            sb.append(line);
	        }
	        in.close();
    	}
    	catch (IOException e) {
    		e.printStackTrace();
    	}

    	// should this be initialized to an empty json object?
        JSONObject result = null;

        try {
        	result = new JSONObject(sb.toString());
        }
        catch (JSONException e) {
            Log.e(getClass().getName(), "Error parsing json response", e);
        }

        return result;
    }
}

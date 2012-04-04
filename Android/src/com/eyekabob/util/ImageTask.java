package com.eyekabob.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageTask extends AsyncTask<String, Object, Bitmap> {
	private ImageView iv;
	private Map<URL, Bitmap> cache;

	@Override
	protected Bitmap doInBackground(String... urls) {
	    HttpURLConnection connection = null;
	    InputStream is = null;
	    Bitmap result = null;
	    try {
	        URL url = new URL((String) urls[0]);
	        if (cache.containsKey(url)) {
	        	return cache.get(url);
	        }
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        is = connection.getInputStream();
	        result = BitmapFactory.decodeStream(is);
	        cache.put(url, result);
	        is.close();
	    }
	    catch (Exception e) {
	        Log.e(getClass().getName(), "Exception while getting event image", e);
	    }
	    finally {
	        try {
	            if (is != null) {
	                is.close();
	            }
	            if (connection != null) {
	                connection.disconnect();
	            }
	        } catch (IOException e) {
	            Log.e(getClass().getName(), "Either unable to close input stream or stop connection", e);
	        }
	    }

	    return result;
	}

	@Override
    protected void onPostExecute(Bitmap result) {
        iv.setImageBitmap(result);
        iv.invalidate();
    }

	public void setImageView(ImageView iv) {
		this.iv = iv;
	}
	public void setCache(Map<URL, Bitmap> cache) {
		this.cache = cache;
	}
}
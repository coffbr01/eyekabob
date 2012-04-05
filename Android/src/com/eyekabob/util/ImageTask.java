package com.eyekabob.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageTask extends AsyncTask<URL, Object, Drawable> {
	private ImageView iv;
	private Map<URL, SoftReference<Drawable>> cache;

	@Override
	protected Drawable doInBackground(URL... urls) {
	    HttpURLConnection connection = null;
	    InputStream is = null;
	    Drawable result = null;
	    try {
	        URL url = urls[0];
	        if (cache.containsKey(url)) {
	        	result = cache.get(url).get();
	        	if (result != null) {
	        		return result;
	        	}
	        }
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        is = connection.getInputStream();
	        
	        result = Drawable.createFromStream(is, url.toString());
	        cache.put(url, new SoftReference<Drawable>(result));
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
    protected void onPostExecute(Drawable result) {
        iv.setImageDrawable(result);

        // TODO: this height/width doesn't belong here.
        LayoutParams params = iv.getLayoutParams();
        params.width = 200;
        params.height = 200;
        iv.setLayoutParams(params);

        iv.invalidateDrawable(result);
    }

	public void setImageView(ImageView iv) {
		this.iv = iv;
	}
	public void setCache(Map<URL, SoftReference<Drawable>> cache) {
		this.cache = cache;
	}
}
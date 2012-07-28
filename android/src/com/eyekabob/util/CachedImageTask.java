/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class CachedImageTask extends ImageTask {
    private ImageView iv;
    private Map<URL, SoftReference<Bitmap>> cache;

    @Override
    protected Bitmap doInBackground(URL... urls) {
        HttpURLConnection connection = null;
        InputStream is = null;
        Bitmap result = null;
        try {
            URL url = urls[0];
            if (cache.containsKey(url)) {
                result = cache.get(url).get();
                if (result != null) {
                    return result;
                }
            }
            is = (InputStream)url.getContent();
            result = BitmapFactory.decodeStream(is);
            cache.put(url, new SoftReference<Bitmap>(result));
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

        // TODO: this height/width doesn't belong here.
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        params.width = 200;
        params.height = 200;
        iv.setLayoutParams(params);
    }

    public void setCache(Map<URL, SoftReference<Bitmap>> cache) {
        this.cache = cache;
    }

    public void setImageView(ImageView iv) {
        this.iv = iv;
    }
}
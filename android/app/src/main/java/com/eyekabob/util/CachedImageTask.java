/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.util;

import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;

public class CachedImageTask extends ImageTask {
    private ImageView iv;
    private Map<URL, SoftReference<Bitmap>> cache;

    @Override
    protected Bitmap doInBackground(URL... urls) {
        Bitmap result;
        URL url = urls[0];
        if (cache.containsKey(url)) {
            result = cache.get(url).get();
            if (result != null) {
                // Image was in cache, so return it.
                return result;
            }
        }

        // Make request.
        result = super.doInBackground(urls);

        // Store image in cache.
        cache.put(url, new SoftReference<Bitmap>(result));
        return result;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        iv.setImageBitmap(result);

        // TODO: this height/width doesn't belong here.
        ViewGroup.LayoutParams params = iv.getLayoutParams();
        params.width = 150;
        params.height = 150;
        iv.setLayoutParams(params);
    }

    public void setCache(Map<URL, SoftReference<Bitmap>> cache) {
        this.cache = cache;
    }

    public void setImageView(ImageView iv) {
        this.iv = iv;
    }
}
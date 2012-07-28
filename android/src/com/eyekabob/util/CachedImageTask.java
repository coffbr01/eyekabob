/**
 * © 2012 Brien Coffield
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
        URL url = urls[0];
        if (cache.containsKey(url)) {
            Bitmap result = cache.get(url).get();
            if (result != null) {
                return result;
            }
        }

        return super.doInBackground(urls);
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
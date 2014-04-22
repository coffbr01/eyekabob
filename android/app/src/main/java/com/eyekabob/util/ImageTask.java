/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageTask extends AsyncTask<URL, Void, Bitmap> {
    @Override
    protected Bitmap doInBackground(URL... urls) {
        InputStream is = null;
        Bitmap result = null;
        try {
            URL url = urls[0];
            is = (InputStream)url.getContent();
            result = BitmapFactory.decodeStream(is);
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
            } catch (IOException e) {
                Log.e(getClass().getName(), "Either unable to close input stream or stop connection", e);
            }
        }

        return result;
    }
}
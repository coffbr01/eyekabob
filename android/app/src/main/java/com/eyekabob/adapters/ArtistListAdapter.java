/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.eyekabob.R;
import com.eyekabob.models.Artist;
import com.eyekabob.util.CachedImageTask;
import com.eyekabob.util.EyekabobHelper;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ArtistListAdapter extends ArrayAdapter<Artist> {
    private Map<URL, SoftReference<Bitmap>> cache = new HashMap<URL, SoftReference<Bitmap>>();

    public ArtistListAdapter(Context context) {
        super(context, R.layout.image_text_list_item);
    }

    public void clearCache() {
        cache.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Artist artistRow = getItem(position);

        // Inflate, reassigning the convertView parameter.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.image_text_list_item, parent, false);

        TextView tv = (TextView)convertView.findViewById(R.id.rowText);
        tv.setText(artistRow.getName());

        URL imageUrl = EyekabobHelper.getLargestImageURL(artistRow.getImageURLs());

        if (imageUrl != null) {
            Log.d(getClass().getName(), "Adding image [" + imageUrl + "] to row [" + artistRow.getName() + "]");
            ImageView iv = (ImageView)convertView.findViewById(R.id.rowImage);
            CachedImageTask task = new CachedImageTask();
            task.setCache(cache);
            task.setImageView(iv);
            task.execute(imageUrl);
        }

        return convertView;
    }
}

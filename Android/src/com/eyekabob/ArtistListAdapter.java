package com.eyekabob;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyekabob.models.Artist;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.ImageTask;

public class ArtistListAdapter extends ArrayAdapter<Artist> {
	private Map<URL, SoftReference<Drawable>> cache = new HashMap<URL, SoftReference<Drawable>>();

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
			ImageTask task = new ImageTask();
			task.setCache(cache);
			task.setImageView(iv);
			task.execute(imageUrl);
		}

		return convertView;
	}
}

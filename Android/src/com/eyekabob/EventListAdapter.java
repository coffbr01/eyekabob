package com.eyekabob;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
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

import com.eyekabob.models.Event;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.ImageTask;

public class EventListAdapter extends ArrayAdapter<Event> {
	private Map<URL, SoftReference<Drawable>> cache = new HashMap<URL, SoftReference<Drawable>>();

	public EventListAdapter(Context context) {
		super(context, R.layout.image_text_list_item, new ArrayList<Event>());
	}

	public void clearCache() {
		cache.clear();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Event eventRow = getItem(position);

		// Inflate, reassigning the convertView parameter.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.image_text_list_item, parent, false);

		// Render the event.
		String text = "";
		if (eventRow.getName() != null && !"".equals(eventRow.getName().trim())) {
			text += eventRow.getName() + "\n";
		}

		if (eventRow.getVenue() != null && !"".equals(eventRow.getVenue().trim())) {
			text += eventRow.getVenue() + "\n";
		}

		if (eventRow.getCity() != null && !"".equals(eventRow.getCity().trim())) {
			text += eventRow.getCity() + "\n";
		}

		if (eventRow.getDate() != null && !"".equals(eventRow.getDate().trim())) {
			text += eventRow.getDate();
			if (eventRow.getDistance() != -1) {
				text += "\n";
			}
		}

		text += eventRow.getDistance() + " mi";

		TextView tv = (TextView)convertView.findViewById(R.id.rowText);
		tv.setText(text);

		URL imageUrl = EyekabobHelper.getLargestImageURL(eventRow.getImageURLs());

		if (imageUrl != null) {
			Log.d(getClass().getName(), "Adding image [" + imageUrl + "] to row [" + eventRow.getName() + "]");
			ImageView iv = (ImageView)convertView.findViewById(R.id.rowImage);
			ImageTask task = new ImageTask();
			task.setCache(cache);
			task.setImageView(iv);
			task.execute(imageUrl);
		}

		return convertView;
	}
}

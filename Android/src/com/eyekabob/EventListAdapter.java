package com.eyekabob;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eyekabob.models.Event;
import com.eyekabob.util.ImageTask;

public class EventListAdapter extends ArrayAdapter<Event> {
	protected Map<URL, Bitmap> bitmapCache = new HashMap<URL, Bitmap>();

	public EventListAdapter(Context context, int resource, List<Event> objects) {
		super(context, resource, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Event eventRow = getItem(position);

		// Inflate unilaterally, reassigning the convertView parameter.
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

		if (eventRow.getImage() != null && !"".equals(eventRow.getImage().trim())) {
			Log.d(getClass().getName(), "Adding image [" + eventRow.getImage() + "] to row [" + eventRow.getName() + "]");
			ImageView iv = (ImageView)convertView.findViewById(R.id.rowImage);
			ImageTask task = new ImageTask();
			task.setImageView(iv);
			task.setCache(bitmapCache);
			task.execute(eventRow.getImage());
		}

		return convertView;
	}
}

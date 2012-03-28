package com.eyekabob;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventsAdapter extends ArrayAdapter<EventRow> {
	protected Map<URL, Bitmap> bitmapCache = new HashMap<URL, Bitmap>();

	public EventsAdapter(Context context, int resource, List<EventRow> objects) {
		super(context, resource, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		EventRow eventRow = getItem(position);

		// Inflate unilaterally, ignoring the convertView parameter.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.events_list_item, parent, false);

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

		TextView tv = (TextView)convertView.findViewById(R.id.eventRowText);
		tv.setText(text);

		if (eventRow.getImage() != null && !"".equals(eventRow.getImage().trim())) {
			Log.d(getClass().getName(), "Adding image [" + eventRow.getImage() + "] to row [" + eventRow.getName() + "]");
			ImageView iv = (ImageView)convertView.findViewById(R.id.eventRowImage);
			ImageTask task = new ImageTask();
			task.setImageView(iv);
			task.execute(eventRow.getImage());
		}

		return convertView;
	}

	private class ImageTask extends AsyncTask<String, Object, Bitmap> {
		private ImageView iv;

		@Override
		protected Bitmap doInBackground(String... urls) {
		    HttpURLConnection connection = null;
		    InputStream is = null;
		    Bitmap result = null;
		    try {
		        URL url = new URL((String) urls[0]);
		        if (bitmapCache.containsKey(url)) {
		        	return bitmapCache.get(url);
		        }
		        connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        is = connection.getInputStream();
		        result = BitmapFactory.decodeStream(is);
		        bitmapCache.put(url, result);
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
	}

}

package com.eyekabob;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eyekabob.models.Artist;

public class ArtistListAdapter extends ArrayAdapter<Artist> {
	protected Map<URL, Bitmap> bitmapCache = new HashMap<URL, Bitmap>();

	public ArtistListAdapter(Context context) {
		super(context, R.layout.image_text_list_item);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Artist artistRow = getItem(position);

		// Inflate unilaterally, reassigning the convertView parameter.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.image_text_list_item, parent, false);

        TextView tv = (TextView)convertView.findViewById(R.id.rowText);
        tv.setText(artistRow.getName());

		return convertView;
	}
}

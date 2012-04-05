package com.eyekabob;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eyekabob.models.Venue;
import com.eyekabob.util.EyekabobHelper;

public class VenueListAdapter extends ArrayAdapter<Venue> {

	public VenueListAdapter(Context context) {
		super(context, R.layout.list_item);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Venue venueRow = getItem(position);

		// Inflate, reassigning the convertView parameter.
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        String rendered = "";
        if (venueRow.getName() != null) {
        	rendered += venueRow.getName() + "\n";
        }

        if (venueRow.getStreet() != null) {
        	rendered += venueRow.getStreet();
        	if (venueRow.getCity() == null) {
        		rendered += "\n";
        	}
        }

        if (venueRow.getCity() != null) {
        	if (venueRow.getStreet() != null) {
        		rendered += ", " + venueRow.getCity();
        	}
        	else {
        		rendered += venueRow.getCity();
        	}
        	rendered += "\n";
        }

        if (venueRow.getLat() != null && venueRow.getLon() != null) {
        	EyekabobHelper.getDistance(venueRow.getLat(), venueRow.getLon(), convertView.getContext().getApplicationContext());
        }

        ((TextView)convertView).setText(rendered);

		return convertView;
	}
}

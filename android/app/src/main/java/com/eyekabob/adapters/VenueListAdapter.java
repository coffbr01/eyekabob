/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
package com.eyekabob.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eyekabob.R;
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
            rendered += venueRow.getName();
        }

        if (venueRow.getStreet() != null) {
            rendered += "\n" + venueRow.getStreet();
        }

        if (venueRow.getCity() != null) {
            if (venueRow.getStreet() != null && !"".equals(venueRow.getStreet())) {
                rendered += ", " + venueRow.getCity();
            }
            else {
                rendered += "\n" + venueRow.getCity();
            }
        }

        if (venueRow.getLat() != null && venueRow.getLon() != null) {
            long dist = EyekabobHelper.getDistance(venueRow.getLat(), venueRow.getLon(), convertView.getContext().getApplicationContext());
            if (dist > -1) {
                rendered += "\n" + String.valueOf(dist) + " mi";
            }
        }

        ((TextView)convertView).setText(rendered);

        return convertView;
    }
}

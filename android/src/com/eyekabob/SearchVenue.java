/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.eyekabob.util.EyekabobHelper;

import java.util.HashMap;
import java.util.Map;

public class SearchVenue extends EyekabobActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchvenueactivity);

        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        findByVenue.requestFocus();
    }

    public void findByVenueHandler(View v) {
        EditText findByVenue = (EditText)findViewById(R.id.findByVenueInput);
        Map<String, String> params = new HashMap<String, String>();
        params.put("venue", findByVenue.getText().toString());
        Uri uri = EyekabobHelper.LastFM.getUri("venue.search", params);
        Intent intent = new Intent(getApplicationContext(), VenueList.class);
        intent.setData(uri);
        startActivity(intent);
    }
}

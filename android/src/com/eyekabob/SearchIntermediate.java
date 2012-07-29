/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * This view contains buttons that navigate the user to a specific
 * type of search. For example, one button may navigate to a
 * "search by location" view. Another button may navigate to a
 * "search by venue" view.
 */
public class SearchIntermediate extends EyekabobActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchintermediateactivity);
    }

    public void searchByArtistHandler(View v) {
        Intent intent = new Intent(this, SearchArtist.class);
        startActivity(intent);
    }

    public void searchByVenueHandler(View v) {
        Intent intent = new Intent(this, SearchVenue.class);
        startActivity(intent);
    }

    public void searchByLocationHandler(View v) {
        Intent intent = new Intent(this, SearchLocation.class);
        startActivity(intent);
    }
}

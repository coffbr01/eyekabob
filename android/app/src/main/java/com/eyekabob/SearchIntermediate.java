/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.eyekabob.util.EyekabobHelper;

/**
 * This view contains buttons that navigate the user to a specific
 * type of search. For example, one button may navigate to a
 * "search by location" view. Another button may navigate to a
 * "search by venue" view.
 */
public class SearchIntermediate extends EyekabobActivity {

    private View.OnClickListener linksListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.aboutEyekabobButton) {
                Dialog aboutDialog = EyekabobHelper.createAboutDialog(SearchIntermediate.this);
                aboutDialog.show();
            }
            else if (view.getId() == R.id.contactButton) {
                EyekabobHelper.launchEmail(SearchIntermediate.this);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchintermediateactivity);
        findViewById(R.id.aboutEyekabobButton).setOnClickListener(linksListener);
        findViewById(R.id.contactButton).setOnClickListener(linksListener);
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

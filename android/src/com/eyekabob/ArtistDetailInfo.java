/**
 * © 2012 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import com.eyekabob.models.Artist;

public class ArtistDetailInfo extends EyekabobActivity {
    private Artist artist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail);
        Bundle b = this.getIntent().getExtras();
        artist = (Artist) b.getSerializable("artist");
        WebView contentWebView = (WebView)findViewById(R.id.artistContent);
        contentWebView.setBackgroundColor(0x00000000);

        TextView detailHeaderView = (TextView)findViewById(R.id.artistDetailHeader);
        detailHeaderView.setText(artist.getName());

        String contentHtml = "<div style='color:white'>" + artist.getContent() + "</div>";
        contentWebView.loadData(contentHtml, "text/html", "UTF8");
    }
}

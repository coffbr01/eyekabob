/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.eyekabob.models.Artist;
import com.eyekabob.util.DocumentTask;
import com.eyekabob.util.EyekabobHelper;
import com.eyekabob.util.NiceNodeList;

public class ArtistInfo extends EyekabobActivity {
	private Artist artist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artist_info);
		WebView summaryWebView = (WebView)findViewById(R.id.artistSummary);
		summaryWebView.setBackgroundColor(0x00000000);
		artist = (Artist)getIntent().getExtras().get("artist");
		Log.d(getClass().getName(), "Artist name is: " + artist.getName());

		Map<String, String> params = new HashMap<String, String>();
		params.put("mbid", artist.getMbid());
		Uri artistInfoUri = EyekabobHelper.LastFM.getUri("artist.getInfo", params);

		// Send the request for artist info.
		new ArtistRequestTask().execute(artistInfoUri.toString());
	}

	/**
	 * This method is called after the last.fm response is received. It will
	 * parse the XML document response and put attributes on the Artist object.
	 * @param result
	 */
	protected void handleArtistResponse(Document result) {
		NiceNodeList artistNodeList = new NiceNodeList(result.getElementsByTagName("artist").item(0).getChildNodes());
		Map<String, Node> artistNodes = artistNodeList.get("name", "mbid", "url", "bio");
		artist.setName(artistNodes.get("name").getTextContent());
		artist.setMbid(artistNodes.get("mbid").getTextContent());
		artist.setUrl(artistNodes.get("url").getTextContent());

		NiceNodeList bioNodeList = new NiceNodeList(artistNodes.get("bio").getChildNodes());
		Map<String, Node> bioNodes = bioNodeList.get("summary", "content");
		artist.setSummary(bioNodes.get("summary").getTextContent());
		artist.setContent(bioNodes.get("content").getTextContent());

		render();
	}

	/**
	 * Puts attributes from the Artist object into the views.
	 */
	private void render() {
		// TODO: Phil to put rendering code here.

		// Example:
		// ------------------------------------------------------------------------------------
		TextView headerTextView = (TextView)findViewById(R.id.artistHeader);
		headerTextView.setText(artist.getName());

		WebView summaryWebView = (WebView)findViewById(R.id.artistSummary);
		String summaryHtml = "<div style='color:white'>" + artist.getSummary() + "</div>";
		summaryWebView.loadData(summaryHtml, "text/html", "UTF8");
		// ------------------------------------------------------------------------------------
	}

    // Handles the asynchronous request, away from the UI thread.
    private class ArtistRequestTask extends DocumentTask {
    	protected void onPreExecute() {
    		ArtistInfo.this.createDialog(R.string.loading);
    		ArtistInfo.this.showDialog();
    	}
    	protected void onPostExecute(Document result) {
    		ArtistInfo.this.dismissDialog();
    		ArtistInfo.this.handleArtistResponse(result);
    	}
    }
}

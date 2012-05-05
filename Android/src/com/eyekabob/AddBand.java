package com.eyekabob;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Expects "url" extra from intent.
 */
public class AddBand extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addband);
		WebView webView = (WebView)findViewById(R.id.addBandWebView);
		String url = (String)getIntent().getExtras().get("url");
		String html = "<html><head></head><body><iframe src='" + url + "'></iframe></body></html>";
		webView.loadData(html, "text/html", null);
	}
}
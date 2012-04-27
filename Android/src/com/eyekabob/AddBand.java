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
		webView.loadUrl((String)getIntent().getExtras().get("url"));
	}
}
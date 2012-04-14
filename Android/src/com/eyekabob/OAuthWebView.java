package com.eyekabob;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.eyekabob.util.EyekabobHelper;

public class OAuthWebView extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		// TODO: this contains foursquare stuff. probably should get it out of this generic class.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        String url =
            EyekabobHelper.Foursquare.AUTHENTICATE_URL +
                "?client_id=" + EyekabobHelper.Foursquare.CLIENT_ID +
                "&response_type=token" +
                "&redirect_uri=" + EyekabobHelper.Foursquare.CALLBACK_URL;

        // If authentication works, we'll get redirected to a url with a pattern like:  
        //
        //    http://YOUR_REGISTERED_REDIRECT_URI/#access_token=ACCESS_TOKEN
        //
        // We can override onPageStarted() in the web client and grab the token out.
        WebView webview = (WebView)findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                String fragment = "#access_token=";
                int start = url.indexOf(fragment);
                if (start > -1) {
                    // You can use the accessToken for api calls now.
                    String accessToken = url.substring(start + fragment.length(), url.length());
        			
                    Log.d(OAuthWebView.class.getName(), "OAuth complete, token: [" + accessToken + "].");
                    Toast.makeText(OAuthWebView.this, "Logged in successfully", Toast.LENGTH_SHORT).show();

                    Object callbackClass = OAuthWebView.this.getIntent().getExtras().get("callbackClass");
                    if (callbackClass instanceof Class<?>) {
	                    Intent callbackIntent = new Intent(OAuthWebView.this, (Class<?>)callbackClass);
	                    OAuthWebView.this.startActivity(callbackIntent);
                    }
                }
            }
        });
        webview.loadUrl(url);
    }

}

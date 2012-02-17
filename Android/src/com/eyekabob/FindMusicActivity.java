package com.eyekabob;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class FindMusicActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findmusicactivity);
    }

    public void findByArtistHandler(View v) {
        EditText findByArtist = (EditText)findViewById(R.id.findByArtistInput);
        String artist = findByArtist.getText().toString();
        URL url = null;
		try {
			url = new URL(EyekabobHelper.LastFM.SERVICE_URL + "?method=artist.getEvents&api_key=" + EyekabobHelper.LastFM.API_KEY + "&artist=" + artist);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		new ArtistTask().execute(url);

    }

    private class ArtistTask extends AsyncTask<URL, Void, String> {
    	protected String doInBackground(URL... urls) {
    		return doRequest(urls[0]);
    	}

    	protected void onPostExecute(String result) {
    		Toast.makeText(getApplicationContext(), result, 50000).show();
    	}
        public String doRequest(URL url) {
        	HttpURLConnection urlConnection = null;
        	StringBuffer out = null;
    		try {
    			urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                out = new StringBuffer();
                byte[] b = new byte[4096];
                for (int n; (n = is.read(b)) != -1;) {
                    out.append(new String(b, 0, n));
                }
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
            finally {
                urlConnection.disconnect();
            }

            return out.toString();
        }
    }
}

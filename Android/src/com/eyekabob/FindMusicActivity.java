package com.eyekabob;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
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

        HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = getStringResponse(in);
            Toast.makeText(v.getContext(), result, 8000).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
        finally {
            urlConnection.disconnect();
        }
    }

    public String getStringResponse(InputStream is) throws IOException {
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for (int n; (n = is.read(b)) != -1;) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}

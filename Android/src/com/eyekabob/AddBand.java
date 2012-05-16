package com.eyekabob;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.eyekabob.util.EyekabobHelper;

public class AddBand extends EyekabobActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addband);
        Spinner genreSpinner = (Spinner)findViewById(R.id.genreSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.genres_array, R.layout.list_item);
		genreSpinner.setAdapter(adapter);
	}

	public void submit(View v) {
        Spinner genreSpinner = (Spinner)findViewById(R.id.genreSpinner);
	    EditText nameEditText = (EditText)findViewById(R.id.artistNameEditText);
	    EditText urlEditText = (EditText)findViewById(R.id.urlEditText);
	    EditText bioEditText = (EditText)findViewById(R.id.artistDescriptionEditText);

        String genre = (String)genreSpinner.getSelectedItem();
	    String name = nameEditText.getText().toString();
	    String url = urlEditText.getText().toString();
	    String bio = bioEditText.getText().toString();

	    if (getResources().getString(R.string.genre).equals(genre)) {
	        // TODO: make better validation message.
	        Toast.makeText(this, "Select a genre", Toast.LENGTH_LONG).show();
	        return;
	    }

	    if (name == null || "".equals(name.trim())) {
	        Toast.makeText(this, "Enter artist name", Toast.LENGTH_LONG).show();
	        return;
	    }

	    Map<String, String> params = new HashMap<String, String>();
	    params.put("genre", genre);
	    params.put("name", name);
	    params.put("url", url);
	    params.put("bio", bio);
	    URI uri = EyekabobHelper.WebService.getURI("artist", params);
	    new AddArtistTask().execute(uri);
	}

	public class AddArtistTask extends AsyncTask<URI, Void, JSONObject> {
	    @Override
	    protected void onPreExecute() {
            AddBand.this.createDialog(R.string.adding_artist);
            AddBand.this.showDialog();
        }

	    @Override
        protected void onPostExecute(JSONObject result) {
            AddBand.this.dismissDialog();
            String artist = result.optString("artist");

            if (artist == null) {
                Toast.makeText(AddBand.this, "Artist could not be added", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(AddBand.this, "Artist " + artist + " added", Toast.LENGTH_SHORT).show();
            AddBand.this.startActivity(new Intent(AddBand.this, Home.class));
            AddBand.this.finish();
        }

	    @Override
        protected JSONObject doInBackground(URI... uri) {
	        HttpClient client = new DefaultHttpClient();
	        HttpRequestBase request = new HttpPost(uri[0]);
	        StringBuffer sb = null;

	        try {
	            HttpResponse response = client.execute(request);
	            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	            sb = new StringBuffer();
	            String line = null;
	            while ((line = in.readLine()) != null) {
	                sb.append(line);
	            }
	            in.close();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }

	        JSONObject result = new JSONObject();

	        try {
	            result = new JSONObject(sb.toString());
	        }
	        catch (JSONException e) {
	            Log.e(getClass().getName(), "Error parsing json response", e);
	        }

	        return result;
        }
	}
}
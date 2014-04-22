/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
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
        Uri uri = EyekabobHelper.WebService.getURI("artist", "addArtist", params);
        new AddArtistTask().execute(uri);
    }

    public class AddArtistTask extends AsyncTask<Uri, Void, JSONObject> {
        @Override
        protected void onPreExecute() {
            AddBand.this.createDialog(R.string.adding_artist);
            AddBand.this.showDialog();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            AddBand.this.dismissDialog();
            String error = result.optString("error");

            if (error != null && !"".equals(error)) {
                Toast.makeText(AddBand.this, error, Toast.LENGTH_SHORT).show();
                Toast.makeText(AddBand.this, result.optString("api"), Toast.LENGTH_SHORT).show();
                Toast.makeText(AddBand.this, result.optString("method"), Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(AddBand.this, "Artist added!!", Toast.LENGTH_SHORT).show();
            AddBand.this.startActivity(new Intent(AddBand.this, Home.class));
            AddBand.this.finish();
        }

        @Override
        protected JSONObject doInBackground(Uri... uri) {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(URI.create(uri[0].toString()));
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
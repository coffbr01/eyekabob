package com.eyekabob;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class ArtistResults extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artistresultsactivity);
        Uri uri = this.getIntent().getData();
        new RequestTask(this).execute(uri.toString());
        
    }
    // Handles the asynchronous request, away from the UI thread.
    private class RequestTask extends AsyncTask<String, Void, Document> {
    	private Activity activity;
    	private Dialog alertDialog;
    	public RequestTask(Activity activity) {
    		this.activity = activity;
    		// TODO: use dialogfragment to show dialog
    	    Builder builder = new AlertDialog.Builder(activity);
    	    builder.setMessage(R.string.searching);
    	    builder.setCancelable(false);
    	    alertDialog = builder.create();
    	    alertDialog.setOwnerActivity(activity);
    	}
    	protected void onPreExecute() {
    		alertDialog.show();
    	}
    	protected Document doInBackground(String... uris) {
    		return doRequest(uris[0]);
    	}
    	protected void onPostExecute(Document result) {
    		alertDialog.dismiss();
    		Toast.makeText(activity, result.toString(), Toast.LENGTH_LONG).show();
    	}
        public Document doRequest(String uri) {
        	Document result = null;
    		try {
    			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    			result = builder.parse(uri);
    		}
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    		catch (ParserConfigurationException e) {
    			e.printStackTrace();
    		}
    		catch (SAXException e) {
    			e.printStackTrace();
    		}

            return result;
        }
    }
}

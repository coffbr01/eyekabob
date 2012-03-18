package com.eyekabob.util;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.os.AsyncTask;

public abstract class DocumentTask extends AsyncTask<String, Void, Document> {
	protected Document doInBackground(String... uris) {
		return doRequest(uris[0]);
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

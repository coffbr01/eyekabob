package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Artist implements Serializable {
	private static final long serialVersionUID = 1293081737855453644L;

	private String name;
	private String mbid;
	private Map<String, URL> imageURLs;
	private String summary;
	private String content;
	private URL url;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMbid() {
		return mbid;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}
	public Map<String, URL> getImageURLs() {
		return imageURLs;
	}
	public void setImageURLs(Map<String, URL> imageURLs) {
		this.imageURLs = imageURLs;
	}
	public void addImageURL(String size, URL url) {
		if (imageURLs == null) {
			imageURLs = new HashMap<String, URL>();
		}

		imageURLs.put(size, url);
	}
	public void addImageURL(String size, String url) {
		URL aUrl = null;
		try {
			aUrl = new URL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		addImageURL(size, aUrl);
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public void setUrl(String url) {
		try {
			this.url = new URL(url);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
}

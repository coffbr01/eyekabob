package com.eyekabob.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class Artist {
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

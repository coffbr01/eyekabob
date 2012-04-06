package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Event implements Serializable {
	private static final long serialVersionUID = -6773600490438197194L;

	private Map<String, URL> imageURLs;
	private String id;
	private String name;
	private String venue;
	private String city;
	private String date;
	private String lat;
	private String lon;

	public Map<String, URL> getImageURLs() {
		return imageURLs;
	}
	public void setImage(Map<String, URL> imageURLs) {
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

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
}

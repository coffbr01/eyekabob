package com.eyekabob.models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Event {
	private Map<String, URL> imageURLs;
	private String name;
	private String venue;
	private String city;
	private String date;
	private long distance = -1;//TODO: this doesn't make sense

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

	public long getDistance() {
		return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
}

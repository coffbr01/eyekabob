/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Event model that holds the information for a particular event.
 * This should hold things like time, place, artists.
 */
public class Event implements Serializable {
	private static final long serialVersionUID = -6773600490438197194L;

	/**
	 * The URLs for the images of this artist. The keys
	 * will be sizes, i.e. "small", "medium", "large".
	 */
	private Map<String, URL> imageURLs;

	/**
	 * Last.fm ID for the event.
	 */
	private String id;

	/**
	 * Common name of the event. "Rock Fest" or "Jam at corner bar"
	 * might be possible values for this field.
	 */
	private String name;

	/**
	 * Name of the venue hosting the event. "Corner bar" is a legitimate
	 * value for this field.
	 */
	private String venue;

	/**
	 * Name of the city where the venue is.
	 */
	private String city;

	/**
	 * Date and time of the event, already formatted.
	 */
	private String date;

	/**
	 * Latitude.
	 */
	private String lat;

	/**
	 * Longitude.
	 */
	private String lon;

	/**
	 * The URLs for the images of this artist. The keys
	 * will be sizes, i.e. "small", "medium", "large".
	 */
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

	/**
	 * Common name of the event. "Rock Fest" or "Jam at corner bar"
	 * might be possible values for this field.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Name of the venue hosting the event. "Corner bar" is a legitimate
	 * value for this field.
	 */
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}

	/**
	 * Name of the city where the venue is.
	 */
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Date and time of the event, already formatted.
	 */
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Last.fm ID for the event.
	 */
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Latitude.
	 */
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}

	/**
	 * Longitude.
	 */
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
}

/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Venue model that holds attributes of a venue. This holds things
 * like the venue name and its location.
 */
public class Venue implements Serializable {
	private static final long serialVersionUID = -5136924774088920040L;

	/**
	 * Last.fm ID for the venue.
	 */
	private Integer id;

	/**
	 * Common name for the venue. Could be values like "Corner bar"
	 * or "Jim's garage"
	 */
	private String name;

	/**
	 * The city in which the venue resides.
	 */
	private String city;

	/**
	 * The country in which the venue resides.
	 */
	private String country;

	/**
	 * The street on which the venue resides. This typically
	 * includes the address as well, i.e. "1800 N Main St."
	 */
	private String street;

	/**
	 * Zip code of the venue.
	 */
	private String postalCode;

	/**
	 * Latitude.
	 */
	private String lat;

	/**
	 * Longitude.
	 */
	private String lon;

	/**
	 * Venue's website.
	 */
	private URL url;

	/**
	 * Last.fm ID for the venue.
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Common name for the venue. Could be values like "Corner bar"
	 * or "Jim's garage"
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The city in which the venue resides.
	 */
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * The country in which the venue resides.
	 */
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * The street on which the venue resides. This typically
	 * includes the address as well, i.e. "1800 N Main St."
	 */
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Zip code of the venue.
	 */
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	/**
	 * Venue's website.
	 */
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

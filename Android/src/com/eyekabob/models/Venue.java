package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class Venue implements Serializable {
	private static final long serialVersionUID = -5136924774088920040L;

	private Integer id;
	private String name;
	private String city;
	private String country;
	private String street;
	private String postalCode;
	private Double lat;
	private Double lon;
	private URL url;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
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

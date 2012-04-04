package com.eyekabob.models;

public class Event {
	private String image;
	private String name;
	private String venue;
	private String city;
	private String date;
	private long distance = -1;//todo this doesn't make sense

	public Event() {}

	public Event(String image, String name, String venue, String city, String date, long distance) {
		this.image = image;
		this.name = name;
		this.venue = venue;
		this.city = city;
		this.date = date;
		this.distance = distance;
	}

	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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

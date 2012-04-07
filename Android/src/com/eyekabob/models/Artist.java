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
 * Artist model that holds attributes about a particular artist.
 * This should hold things like an artist's name and its music brainz ID.
 */
public class Artist implements Serializable {
	private static final long serialVersionUID = 1293081737855453644L;

	/**
	 * Common name for the artist.
	 */
	private String name;

	/**
	 * Music brainz ID.
	 */
	private String mbid;

	/**
	 * The URLs for the images of this artist. The keys
	 * will be sizes, i.e. "small", "medium", "large".
	 */
	private Map<String, URL> imageURLs;

	/**
	 * A (usually HTML) summary of the artist.
	 */
	private String summary;

	/**
	 * A long version of summary.
	 */
	private String content;

	/**
	 * Artist's web site.
	 */
	private URL url;

	/**
	 * Common name for the artist.
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Music brainz ID.
	 */
	public String getMbid() {
		return mbid;
	}
	public void setMbid(String mbid) {
		this.mbid = mbid;
	}

	/**
	 * The URLs for the images of this artist. The keys
	 * will be sizes, i.e. "small", "medium", "large".
	 */
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

	/**
	 * A (usually HTML) summary of the artist.
	 */
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * A long version of summary.
	 */
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Artist's web site.
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

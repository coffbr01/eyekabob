/**
 * © 2014 Brien Coffield
 *
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
package com.eyekabob.models;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Event model that holds the information for a particular event.
 * This should hold things like time, place, artists.
 */
public class Event implements Serializable {
    private static final long serialVersionUID = -6773600490438197194L;

    private Map<String, URL> imageURLs;
    private Map<String, URL> vendors;
    private List<Artist> openingArtists;
    private Artist headliner;
    private Venue venue;
    private String id;
    private String name;
    private String date;

    /**
     * The URLs for the images of this artist. The keys
     * will be sizes, i.e. "small", "medium", "large".
     * @return
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
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Venue where the event will be held. See com.eyekabob.models.Venue
     * for more information about venues.
     * @return
     */
    public Venue getVenue() {
        return venue;
    }
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * Date and time of the event, already formatted.
     * @return
     */
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Last.fm ID for the event.
     * @return
     */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
     * A list of vendors for the event. These are not usually
     * provided for most events. But if they are, they will be
     * URLs for TicketMaster and similar ticket vendors.
     * @return
     */
    public Map<String, URL> getVendors() {
        return vendors;
    }
    public void setVendors(Map<String, URL> vendors) {
        this.vendors = vendors;
    }
    public void addVendor(String vendorName, URL url) {
        if (vendors == null) {
            vendors = new HashMap<String, URL>();
        }

        vendors.put(vendorName, url);
    }
    public void addVendor(String vendorName, String url) {
        URL aUrl = null;
        try {
            aUrl = new URL(url);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        addVendor(vendorName, aUrl);
    }

    /**
     * A list of artists that will perform at the event.
     * These are artists that are opening for the headliner.
     * @return
     */
    public List<Artist> getOpeningArtists() {
        return openingArtists;
    }
    public void setOpeningArtists(List<Artist> artists) {
        this.openingArtists = artists;
    }
    public void addOpeningArtist(Artist artist) {
        if (openingArtists == null) {
            openingArtists = new ArrayList<Artist>();
        }
        openingArtists.add(artist);
    }

    /**
     * The main artist that will perform at a given event.
     * @return
     */
    public Artist getHeadliner() {
        return headliner;
    }
    public void setHeadliner(Artist headliner) {
        this.headliner = headliner;
    }
}

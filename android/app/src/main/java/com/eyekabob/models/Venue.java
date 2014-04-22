/**
 * © 2014 Brien Coffield
 *
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

    private String id;
    private String name;
    private String city;
    private String country;
    private String street;
    private String postalCode;
    private String lat;
    private String lon;
    private URL url;

    /**
     * ID for the venue.
     * @return
     */
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Common name for the venue. Could be values like "Corner bar"
     * or "Jim's garage"
     * @return
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The city in which the venue resides.
     * @return
     */
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * The country in which the venue resides.
     * @return
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
     * @return
     */
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Zip code of the venue.
     * @return
     */
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Latitude.
     * @return
     */
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     * Longitude.
     * @return
     */
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }

    /**
     * Venue's website.
     * @return
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

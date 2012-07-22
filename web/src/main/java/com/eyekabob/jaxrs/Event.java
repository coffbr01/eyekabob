package com.eyekabob.jaxrs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
@Path("/event")
public class Event {
    @GET
    @Produces("application/json")
    public String getResponse(@QueryParam("id") String id, @QueryParam("search") String search) {
        if (id != null && !"".equals(id)) {
            return getResponseByID(id);
        }

        if (search != null && !"".equals(search)) {
            return getResponseBySearchTerm(search);
        }

        return "{error:\"Unable to retrieve event(s). Did you specify either 'id' or 'search'?\"}";
    }

    protected String getResponseByID(String id) {
        try {
            JSONObject event = new JSONObject();
            event.put("id", id);

            JSONObject response = new JSONObject();
            response.put("event", event);

            return response.toString();
        }
        catch (JSONException e) {
            return "{error:\"Unable to get event with ID [" + id + "]\"}";
        }
    }

    protected String getResponseBySearchTerm(String search) {
        try {
            JSONObject response = new JSONObject();
            response.put("search", search);

            JSONArray events = new JSONArray();
            response.put("events", events);

            return response.toString();
        }
        catch (JSONException e) {
            return "{error:\"Unable to get event with search term [" + search + "]\"}";
        }
    }
}

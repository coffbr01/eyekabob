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
    private static final String DEFAULT_SEARCH_LIMIT = "50";

    /**
     * Either id or search is required. If both are provided, id will be used.
     * If given an id, the event that matches the id will be returned.
     * If given a search term, a list of events that match the query will be returned.
     *
     * The number of results returned from a search query is 50 by default, although that
     * can be increased by specifying limit. Use limit in conjunction with the search parameter.
     *
     * @param id The unique ID of an event.
     * @param search A search term to use against the events table.
     * @param limit The maximum number of event records to return. Use in conjunction with the search parameter.
     * @return
     */
    @GET
    @Produces("application/json")
    public String getResponse(@QueryParam("id") String id, @QueryParam("search") String search, @QueryParam("limit") String limit) {
        if (id != null && !"".equals(id)) {
            return getResponseByID(id);
        }

        if (search != null && !"".equals(search)) {
            return getResponseBySearchTerm(search, limit);
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

    protected String getResponseBySearchTerm(String search, String limit) {
        if (limit == null) {
            limit = DEFAULT_SEARCH_LIMIT;
        }

        try {
            JSONObject response = new JSONObject();
            response.put("search", search);

            JSONArray events = new JSONArray();
            if (events.length() < Integer.parseInt(limit)) {
                JSONObject event = new JSONObject();
                event.put("title", "some " + search + " name");
                events.put(event);
            }
            response.put("events", events);

            return response.toString();
        }
        catch (JSONException e) {
            return "{error:\"Unable to get event with search term [" + search + "]\"}";
        }
    }
}

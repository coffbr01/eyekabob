package com.eyekabob.jaxrs;

import com.eyekabob.db.DBUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
@Path("/event")
public class Event {
    private static final int DEFAULT_SEARCH_LIMIT = 50;

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
     * @return response
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
        JSONObject response = new JSONObject();

        if (id == null) {
            return getJSONError("Error", "ID was [" + id + "]");
        }

        int intId;
        try {
            intId = Integer.parseInt(id);
        }
        catch (NumberFormatException e) {
            return getJSONError("Error", "ID [" + id + "] was not an int");
        }

        Connection conn = null;
        String error = null;
        try {
            JSONObject event = new JSONObject();
            String query = "SELECT id,name FROM event WHERE id='?'";
            conn = DBUtils.getConn();
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setInt(1, intId);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                // There will only be one row for an event ID query, so the while loop means nothing.
                event.put("id", resultSet.getInt("id"));
                event.put("name", resultSet.getString("name"));
            }

            response.put("event", event);
        }
        catch (SQLException e) {
            error = getJSONError(SQLException.class.getSimpleName(), e.getMessage());
        }
        catch (JSONException e) {
            error = getJSONError(JSONException.class.getSimpleName(), e.getMessage());
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    error = getJSONError(SQLException.class.getSimpleName(), e.getMessage());
                }
            }
        }

        if (error != null) {
            return error;
        }

        return response.toString();
    }

    protected String getResponseBySearchTerm(String search, String limit) {
        JSONObject response = new JSONObject();

        int intLimit;
        try {
            intLimit = Integer.parseInt(limit);
        }
        catch (NumberFormatException e) {
            intLimit = DEFAULT_SEARCH_LIMIT;
        }

        Connection conn = null;
        String error = null;
        try {
            response.put("search", search);

            conn = DBUtils.getConn();
            String query = "SELECT id,name FROM event WHERE name LIKE '%?%' LIMIT ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, search);
            statement.setInt(2, intLimit);
            ResultSet resultSet = statement.executeQuery();

            JSONArray events = new JSONArray();
            while (resultSet.next()) {
                JSONObject event = new JSONObject();
                event.put("id", resultSet.getInt("id"));
                event.put("name", resultSet.getString("name"));
                events.put(event);
            }

            response.put("events", events);
        }
        catch (SQLException e) {
            error = getJSONError(SQLException.class.getSimpleName(), e.getMessage());
        }
        catch (JSONException e) {
            error = getJSONError(JSONException.class.getSimpleName(), e.getMessage());
        }
        finally {
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException e) {
                    error = getJSONError(SQLException.class.getSimpleName(), e.getMessage());
                }
            }
        }

        if (error != null) {
            return error;
        }

        return response.toString();
    }

    private String getJSONError(String title, String message) {
        return "{error:\"" + title + ": " + message + "\"}";
    }
}

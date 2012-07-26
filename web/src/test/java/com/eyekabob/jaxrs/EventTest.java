package com.eyekabob.jaxrs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

// @TODO Unignore the tests. They need some DB mocking or to be made into integration tests.
public class EventTest {

    @Test
    public void testGetResponseNoIDNoSearch() throws Exception {
        String result = new Event().getResponse(null, null, null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = new Event().getResponse("", null, null);
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = new Event().getResponse(null, "", null);
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = new Event().getResponse("", "", null);
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));
    }

    @Ignore
    @Test
    public void testGetResponseID() throws Exception {
        String result = new Event().getResponse("0", null, null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals("anID", event.get("id"));
    }

    @Ignore
    @Test
    public void testGetResponseSearch() throws Exception {
        String result = new Event().getResponse(null, "aSearchTerm", null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("search"));
        assertEquals("aSearchTerm", jsonResult.get("search"));

        assertTrue(jsonResult.has("events"));
        JSONArray resultEvents = jsonResult.getJSONArray("events");
        assertEquals(1, resultEvents.length());
    }

    @Ignore
    @Test
    public void testGetResponseIDSearch() throws Exception {
        String result = new Event().getResponse("0", "aSearchTerm", null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));
        assertFalse(jsonResult.has("search"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals("anID", event.get("id"));
    }

    @Ignore
    @Test
    public void testGetResponseByID() throws Exception {
        String result = new Event().getResponseByID("0");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals("anID", event.get("id"));
    }

    @Ignore
    @Test
    public void testGetResponseBySearchTerm() throws Exception {
        String result = new Event().getResponseBySearchTerm("aSearchTerm", null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("search"));
        assertEquals("aSearchTerm", jsonResult.get("search"));

        assertTrue(jsonResult.has("events"));
        JSONArray resultEvents = jsonResult.getJSONArray("events");
        assertEquals(1, resultEvents.length());
    }

    @Ignore
    @Test
    public void testLimit() throws Exception {
        String result = new Event().getResponseBySearchTerm("aSearchTerm", "0");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("search"));
        assertEquals("aSearchTerm", jsonResult.get("search"));

        assertTrue(jsonResult.has("events"));
        JSONArray resultEvents = jsonResult.getJSONArray("events");
        assertEquals(0, resultEvents.length());
    }
}

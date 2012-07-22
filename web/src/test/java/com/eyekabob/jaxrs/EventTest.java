package com.eyekabob.jaxrs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;

public class EventTest {
    private Event eventService;

    @Before
    public void before() {
        eventService = new Event();
    }

    @After
    public void after() {
        eventService = null;
    }

    @Test
    public void testGetResponseNoIDNoSearch() throws Exception {
        String result = eventService.getResponse(null, null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = eventService.getResponse("", null);
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = eventService.getResponse(null, "");
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));

        result = eventService.getResponse("", "");
        jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("error"));
    }

    @Test
    public void testGetResponseByID() throws Exception {
        String result = eventService.getResponseByID("anID");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals("anID", event.get("id"));
    }

    @Test
    public void testGetResponseBySearchTerm() throws Exception {
        String result = eventService.getResponseBySearchTerm("aSearchTerm");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("search"));
        assertEquals("aSearchTerm", jsonResult.get("search"));

        assertTrue(jsonResult.has("events"));
        JSONArray resultEvents = jsonResult.getJSONArray("events");
        assertEquals(0, resultEvents.length());
    }
}

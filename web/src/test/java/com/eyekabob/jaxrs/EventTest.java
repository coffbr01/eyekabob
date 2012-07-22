package com.eyekabob.jaxrs;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
}

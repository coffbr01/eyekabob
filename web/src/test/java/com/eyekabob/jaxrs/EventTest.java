package com.eyekabob.jaxrs;

import com.eyekabob.db.DBUtils;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EventTest {

    @Before
    public void before() {
        Mockit.setUpMock(DBUtils.class, MockDBUtils.class);

        // Set up mock data.
        MockDBUtils.result = new ArrayList<Map<String, Object>>();
        Map<String, Object> mockRow = new HashMap<String, Object>();
        mockRow.put("id", 0);
        MockDBUtils.result.add(mockRow);
    }

    @After
    public void after() {
        Mockit.tearDownMocks();
    }

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

    @Test
    public void testGetResponseID() throws Exception {
        String result = new Event().getResponse("0", null, null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals(0, event.get("id"));
    }

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

    @Test
    public void testGetResponseIDSearch() throws Exception {
        String result = new Event().getResponse("0", "aSearchTerm", null);
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));
        assertFalse(jsonResult.has("search"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals(0, event.get("id"));
    }

    @Test
    public void testGetResponseByID() throws Exception {
        String result = new Event().getResponseByID("0");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("event"));

        JSONObject event = jsonResult.getJSONObject("event");
        assertTrue(event.has("id"));

        assertEquals(0, event.get("id"));
    }

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

    @Test
    public void testLimit() throws Exception {
        String result = new Event().getResponseBySearchTerm("aSearchTerm", "0");
        JSONObject jsonResult = new JSONObject(result);
        assertTrue(jsonResult.has("search"));
        assertEquals("aSearchTerm", jsonResult.get("search"));

        assertTrue(jsonResult.has("events"));
        JSONArray resultEvents = jsonResult.getJSONArray("events");
        assertEquals(1, resultEvents.length());
    }

    @MockClass(realClass = DBUtils.class)
    public static class MockDBUtils {
        public static List<Map<String, Object>> result;
        @Mock
        public static List<Map<String, Object>> query(String preparedQuery, Object... preparedArgs) {
            return result;
        }
    }
}

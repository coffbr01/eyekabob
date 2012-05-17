package com.eyekabob.webservice;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Main {
    public static JSONObject processRequest(HttpServletRequest request) {
        JSONObject result = null;

        try {
            result = new JSONObject();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}

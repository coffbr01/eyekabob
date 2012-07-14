package com.eyekabob;

import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
@Path("/main")
public class Main {
    @GET
    @Produces("application/json")
    public String helloWorld() throws JSONException {
        JSONObject response = new JSONObject();
        response.put("response", "Hello, world!");
        return response.toString();
    }
}

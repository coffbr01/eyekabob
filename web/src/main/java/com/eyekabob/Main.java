package com.eyekabob;

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
@Path("/main")
public class Main {
    @GET
    @Produces("application/json")
    public String helloWorld(@QueryParam("param") String param) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("response", "Hello, world! " + param);
        return response.toString();
    }
}

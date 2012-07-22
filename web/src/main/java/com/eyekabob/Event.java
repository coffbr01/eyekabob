package com.eyekabob;

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
    public String getEvent(@QueryParam("eventID") String eventID) {
        return "{eventID: " + eventID + "}";
    }
}

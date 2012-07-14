package com.eyekabob;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/rest/main")
public class Main {
    @GET
    @Produces("text/plain")
    public String helloWorldMethod() {
        return "Hello, world!";
    }
}

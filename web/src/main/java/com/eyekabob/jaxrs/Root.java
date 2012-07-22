package com.eyekabob.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
@Path("")
public class Root {
    /**
     * Handles requests made to the web service root. This should respond with
     * branding and Eyekabob Web Service API documentation.
     * @return
     */
    @GET
    @Produces({"text/html", "text/plain"})
    public String getResponse() {
        URL url = getClass().getClassLoader().getResource("index.html");

        InputStream is;
        try {
            is = url.openStream();
        }
        catch (IOException e) {
            // No index.html. Return some generic text.
            return "Eyekabob Web Services root";
        }

        Scanner scanner = new Scanner(is);
        scanner = scanner.useDelimiter("\\A");
        return scanner.next();
    }
}

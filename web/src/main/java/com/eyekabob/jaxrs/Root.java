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
    @GET
    @Produces("text/html")
    public String getHandler() throws IOException {
        URL url = getClass().getClassLoader().getResource("index.html");
        InputStream is = url.openStream();
        Scanner scanner = new Scanner(is);
        scanner = scanner.useDelimiter("\\A");
        return scanner.next();
    }
}

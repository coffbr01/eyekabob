package com.eyekabob.jaxrs;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * © Copyright 2014 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

/**
 * Registers the web services as part of this application. For example,
 * the Root web service has a path of "" so it will handle all requests
 * made to the root of the web service. Without registering it here, jax-rs
 * would have no idea that Root is meant to handle requests.
 * @see javax.ws.rs.core.Application
 */
public class WebServiceApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(Root.class);
        classes.add(Event.class);
        return classes;
    }
}

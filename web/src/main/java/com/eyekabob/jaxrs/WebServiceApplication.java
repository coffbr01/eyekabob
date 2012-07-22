package com.eyekabob.jaxrs;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
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

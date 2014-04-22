package com.eyekabob.jaxrs;

import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.*;

/**
 * © Copyright 2014 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */
public class WebServiceApplicationTest {
    @Test
    public void testGetClasses() throws Exception {
        Set<Class<?>> classes = new WebServiceApplication().getClasses();
        assertTrue(classes.contains(Root.class));
        assertTrue(classes.contains(Event.class));
    }
}

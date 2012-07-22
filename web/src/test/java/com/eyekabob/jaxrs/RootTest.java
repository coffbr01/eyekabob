package com.eyekabob.jaxrs;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
public class RootTest {
    @Test
    public void testRoot() throws Exception {
        String result = new Root().getResponse();
        assertNotNull(result);
        assertTrue(result.contains("html"));
        assertTrue(result.contains("head"));
        assertTrue(result.contains("body"));
        assertTrue(result.contains("Eyekabob"));
    }
}

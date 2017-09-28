/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oneandone.sshconfig;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author Stephan Fuhrmann
 */
public class ParamsTest {
    @Test
    public void testParseWithoutUser() {
        Params params = Params.parse(new String [] {});
        assertNotNull(params.getUser());
    }
    
    @Test
    public void testParseWithUser() {
        Params params = Params.parse(new String [] {"-user", "foomaster"});
        assertEquals("foomaster", params.getUser());
    }
}

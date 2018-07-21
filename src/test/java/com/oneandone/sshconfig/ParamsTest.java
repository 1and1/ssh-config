/*
 * Copyright 2018 1&1 Internet SE.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oneandone.sshconfig;

import com.oneandone.sshconfig.Params;
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

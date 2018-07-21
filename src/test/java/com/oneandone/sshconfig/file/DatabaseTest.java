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
package com.oneandone.sshconfig.file;

import com.oneandone.sshconfig.file.Database;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.oneandone.sshconfig.bind.Host;
import javax.validation.ValidationException;
import static org.junit.Assert.*;

/**
 * Test for {@link Database}.
 * @author Stephan Fuhrmann
 */
public class DatabaseTest {

    private Host validHost;
    private File tmp;

    @Before
    public void init() throws IOException {
        validHost = new Host();
        validHost.setId(UUID.randomUUID());
        validHost.setName("foo");
        validHost.setIps(new String[] {"127.0.0.1"});
        validHost.setEnabled(true);
        validHost.setFqdn("www.cnn.com");
        validHost.setCreatedAt(new Date());
        validHost.setUpdatedAt(validHost.getCreatedAt());

        tmp = File.createTempFile("sshconfig", ".tmp");
        tmp.delete(); // createTempFile creates empty file
    }

    @After
    public void cleanup() throws IOException {
        tmp.delete();
        tmp = null;
    }


    /** Create new database. */
    @Test
    public void newInstanceWitExisting() throws IOException {
        Database db = Database.fromPath(tmp.toPath());
        Host h = validHost;

        db.update(Collections.singletonList(h));
        assertFalse(tmp.exists());
        db.save();
        assertTrue(tmp.exists());

        Database db2 = Database.fromPath(tmp.toPath());
        assertEquals(1, db2.getList().size());
    }

    /** Create new database. */
    @Test
    public void save() throws IOException {
        Database db = Database.fromPath(tmp.toPath());
        Host h = validHost;
        db.update(Collections.singletonList(h));
        db.save();

        Database db2 = Database.fromPath(tmp.toPath());
        assertEquals(1, db2.getList().size());
        Host h2 = db2.getList().get(0);
        assertEquals(h.getId(), h2.getId());
        assertEquals(h.getName(), h2.getName());
        assertNotNull(h2.getCreatedAt());
        assertNotNull(h2.getUpdatedAt());
        assertEquals(true, h2.getEnabled());
    }

    @Test(expected = ValidationException.class)
    public void saveWithIllegalUser() throws IOException {
        Database db = Database.fromPath(tmp.toPath());
        Host h = validHost;
        h.setUser("No Space In User");
        db.update(Collections.singletonList(h));
        db.save();
    }

    @Test(expected = ValidationException.class)
    public void saveWithIllegalIp() throws IOException {
        Database db = Database.fromPath(tmp.toPath());
        Host h = validHost;
        h.setIps(new String[] {":-)))"});
        db.update(Collections.singletonList(h));
        db.save();
    }
}

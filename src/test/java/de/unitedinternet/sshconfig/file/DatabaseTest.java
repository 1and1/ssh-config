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
package de.unitedinternet.sshconfig.file;

import de.unitedinternet.sshconfig.file.Database;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.junit.Test;
import de.unitedinternet.sshconfig.bind.Host;
import static org.junit.Assert.*;

/**
 * Test for {@link Database}.
 * @author Stephan Fuhrmann
 */
public class DatabaseTest {
    
    private static File tmpFile() throws IOException {
        File tmp = File.createTempFile("sshconfig", ".tmp");
        tmp.delete(); // createTempFile creates empty file
        return tmp;        
    }
    
    /** Create new database. */
    @Test
    public void newInstanceWithNew() throws IOException {
        File tmp = tmpFile();
        Database db = Database.fromPath(tmp.toPath());
        assertFalse(tmp.exists());
        tmp.delete();
    }
    
    /** Create new database. */
    @Test
    public void newInstanceWitExisting() throws IOException {
        File tmp = tmpFile();
        Database db = Database.fromPath(tmp.toPath());
        Host h = new Host();
        
        db.update(Collections.singletonList(h));        
        assertFalse(tmp.exists());
        db.save();
        assertTrue(tmp.exists());
        
        Database db2 = Database.fromPath(tmp.toPath());
        assertEquals(1, db2.getList().size());
        
        tmp.delete();
    }
    
    /** Create new database. */
    @Test
    public void save() throws IOException {
        File tmp = tmpFile();
        Database db = Database.fromPath(tmp.toPath());
        Host h = new Host();
        h.setId(UUID.randomUUID());
        h.setName("foobar");
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
        
        tmp.delete();
    }
}

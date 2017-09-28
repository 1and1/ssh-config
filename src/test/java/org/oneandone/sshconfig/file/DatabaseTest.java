/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oneandone.sshconfig.file;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.junit.Test;
import org.oneandone.sshconfig.bind.Host;
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

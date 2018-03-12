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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import com.oneandone.sshconfig.bind.Host;

/**
 * Stores all {@link Host} records to a file.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Database {
    /** The file to load/store to. */
    private final Path database;

    /** The current version of the host list. */
    private List<Host> list;
    
    private Database(Path database) {
        this.database = database;        
    }
    
    /** Clean up hosts, generate synthetical fields. */
    private void sanitize() {
        for (Host h : list) {
            if (h.getCreatedAt() == null) {
                h.setCreatedAt(new Date());
            }
            if (h.getUpdatedAt() == null) {
                h.setUpdatedAt(h.getCreatedAt());
            }
            if (h.getEnabled() == null) {
                h.setEnabled(Boolean.TRUE);
            }
        }
    }
    
    /** Read database from a file. */
    public static Database fromPath(Path f) throws IOException {
        Database db = new Database(f);
        if (Files.exists(f)) {
            ObjectMapper mapper = new ObjectMapper();
            db.list = mapper.readValue(f.toFile(), new TypeReference<List<Host>>(){});
            db.sanitize();
        } else {
            db.list = new ArrayList<>();
        }
        return db;
    }
    
    /** Save database to a file. 
     * @see #database
     */
    public void save() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
   
        Backup.moveToBackup(database);        
        sanitize();
        mapper.writeValue(database.toFile(), list);
    }
    
    /** Update internal database with the given list of hosts.
     * Hosts will either be updated or inserted.
     */
    public void update(List<Host> in) {
        for (Host h : in) {
            if (list.indexOf(h) == -1) {
                log.info("Adding unknown host {}", h.getFqdn());
                list.add(h);
            } else {
                log.info("Skipping known host {}", h.getFqdn());                
            }
        }
    }

    /** Get a read only list view on the database. */
    public List<Host> getList() {
        return Collections.unmodifiableList(list);
    }
}

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
import de.unitedinternet.sshconfig.bind.Host;
import java.util.Objects;

/**
 * Stores all {@link Host} records to a file.
 * @author Stephan Fuhrmann
 */
@Slf4j
public final class Database {
    /** The file to load/store to. */
    private final Path database;

    /** The current version of the host list. */
    private List<Host> list;

    /** Creates a database referring to the given file system path.
     * @param inDatabase the file system path to relate to.
     */
    private Database(final Path inDatabase) {
        this.database = Objects.requireNonNull(inDatabase);
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

    /** Read database from a file.
     * @param f the path to read the database from.
     * @return the read database if the file exists or an empty database.
     * @throws IOException if the database exists, but couldn't be read.
     */
    public static Database fromPath(final Path f) throws IOException {
        Database db = new Database(f);
        if (Files.exists(f)) {
            ObjectMapper mapper = new ObjectMapper();
            db.list = mapper.readValue(
                    f.toFile(),
                    new TypeReference<List<Host>>() { });
            db.sanitize();
        } else {
            db.list = new ArrayList<>();
        }
        return db;
    }

    /** Save database to a file.
     * @throws IOException if the database could not be written.
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
     * @param in the hosts to update. Known hosts will not be added, unknown
     * hosts will be added.
     */
    public void update(final List<Host> in) {
        for (Host h : in) {
            if (list.indexOf(h) == -1) {
                log.info("Adding unknown host {}", h.getFqdn());
                list.add(h);
            } else {
                log.info("Skipping known host {}", h.getFqdn());
            }
        }
    }

    /** Get a read only list view on the database.
     * @return a read-only view on the list of hosts.
     */
    public List<Host> getList() {
        return Collections.unmodifiableList(list);
    }
}

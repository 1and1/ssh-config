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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple backup function for files.
 * @author Stephan Fuhrmann
 */
@Slf4j
class Backup {
    /** Suffix for the backup file. */
    private final static String BACKUP_SUFFIX = ".bak";
    
    private Backup() {
        // no instance
    }    


    /** Renames the file to a backup name, moving it away.
     * @param p the file to backup / move away.
     */
    public static void moveToBackup(Path p) throws IOException {
        Path parent = p.getParent();
        Path backup = parent.resolve(p.getFileName() + BACKUP_SUFFIX);

        if (!Files.exists(p)) {
            log.debug("Does not exist, not backing up: {}", p);
            return;
        }

        log.debug("Backing up to {}", backup);
        if (Files.exists(backup)) {
            log.debug("Removing backup file in the way {}", backup);
            Files.delete(backup);
        }
        Files.move(p, backup);
    }
}

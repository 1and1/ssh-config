/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oneandone.sshconfig.file;

import java.io.File;
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

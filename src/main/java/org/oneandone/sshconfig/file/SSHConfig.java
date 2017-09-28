/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.oneandone.sshconfig.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.oneandone.sshconfig.bind.Host;

/**
 * The SSH configuration file. Separates the tool-maintained  part
 * and the original human-maintained part.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class SSHConfig {
    /** The charset to encode the config on disc with. */
    private final static String CHARSET = "UTF-8";
    
    /** The file in which the config is located. */
    private Path file;
    
    /** Configuration file lines not to process. */
    private List<String> lines;
    
    /** Configuration file lines to process. */
    private List<String> own;

    private final static String ENTRY_BEGIN_PATTERN = "# <<< BEGIN\\{[a-fA-F0-9-]*\\}";
    private final static String ENTRY_END_PATTERN =   "# >>> END\\{[a-fA-F0-9-]*\\}";
    
    private final static String ENTRY_BEGIN_FORMAT = "# <<< BEGIN{%s}";
    private final static String ENTRY_END_FORMAT =   "# >>> END{%s}";
    
    /** Load the ssh configuration from hard disk. */
    public static SSHConfig fromPath(Path f) throws IOException {
        SSHConfig config = new SSHConfig();
        config.file = f;
        if (Files.exists(f)) {
            List<String> lines = Files.readAllLines(f, Charset.forName(CHARSET));
            config.lines = lines;
        } else {
            config.lines = new ArrayList<>();
        }
        return config;
    }

    /** Save the file to the pre-configured location.
     * */
    public void save() throws IOException {
        save(true, true, file);
    }

    /** Save the file to the defined location.
     *  @param includeOwnPart include the generated host entries,
     *  @param includePlainPart include the non-generated ssh config directives,
     *  @param out the path to store the data in,
     * */
    public void save(boolean includeOwnPart, boolean includePlainPart, Path out) throws IOException {
        Backup.moveToBackup(out);
        List<String> allLines = new ArrayList<>();
        if (includePlainPart) {
            allLines.addAll(lines);
        }
        if (includeOwnPart) {
            allLines.addAll(own);
        }
        Files.write(out, allLines, Charset.forName(CHARSET));
    }

    /** Set the own hosts. */
    public void pushOwn(List<Host> hosts, Optional<String> user) {
        removeOwnEntries();
        own = generateOwnEntries(hosts, user);
    }
    
    /** Generate own entries. */
    private static List<String> generateOwnEntries(List<Host> hosts, Optional<String> user) {
        List<String> result = new ArrayList<>();
        
        for (Host h : hosts) {
            MDC.put("id", h.getId());
            MDC.put("name", h.getName());
            if (! h.getEnabled()) {
                log.info("Skipping {}, disabled", h.getName());
                continue;
            }
            
            List<String> hostData = new ArrayList<>();
            hostData.add(String.format(ENTRY_BEGIN_FORMAT, h.getId().toString()));
            hostData.add(String.format("Host %s", h.getName()));
            hostData.add(String.format("\tHostname %s", h.getFqdn()));
            Stream.of(h.getIps()).forEach(ip -> hostData.add(String.format("\tHostname %s", ip)));            
            if (user.isPresent()) {
                hostData.add(String.format("\tUser %s", user.get()));
            }
            
            hostData.add(String.format(ENTRY_END_FORMAT, h.getId().toString()));
            
            log.debug("Host entry: {}", hostData);
            result.addAll(hostData);
        }
        MDC.remove("id");
        MDC.remove("name");
        
        return result;
    }
    
    /** Remove own entries from {@link #lines}. */
    private void removeOwnEntries() {
        List<Integer> removeIndexes = new ArrayList<>();
        List<String> ownEntries = new ArrayList<>();
        
        Pattern begin = Pattern.compile(ENTRY_BEGIN_PATTERN);
        Pattern end = Pattern.compile(ENTRY_END_PATTERN);
        
        boolean inEntry = false;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            
            if (begin.matcher(line).matches()) {
                inEntry = true;
                log.debug("Found start: {}", line);
            }
            
            if (inEntry) {
                ownEntries.add(line);
                removeIndexes.add(i);
            }
            
            if (end.matcher(line).matches()) {
                inEntry = false;
                log.debug("Found end: {}", line);
            }
        }

        log.debug("Found {} lines to remove", removeIndexes.size());
        
        removeIndexes
                .stream()
                .sorted(Comparator.comparingInt(a -> -a))
                .forEach(i -> lines.remove(i.intValue()));
        
        own = ownEntries;
    }
}

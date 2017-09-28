/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oneandone.sshconfig;

import com.oneandone.sshconfig.file.SSHConfig;
import com.oneandone.sshconfig.file.Database;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import com.oneandone.sshconfig.bind.Host;
import org.slf4j.MDC;
import static java.util.stream.Collectors.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The main program.
 * @author Stephan Fuhrmann
 */
@Slf4j
public class Main {

    private final StatusLine statusLine;

    public Main() {
        statusLine = new StatusLine(System.err);
    }

    /** Discover a list of hosts by their DNS name. Will only
     * return the discovered hosts. The others will be silently
     * dropped.
     */
    private List<Host> discover(List<String> discover) {
        log.debug("Discovering started for {} args", discover.size());
        
        List<Host> hosts = discover
                .stream()
                .parallel()
                .map(discoverMe -> ignorantDiscover(discoverMe))
                .filter(h -> h.isPresent())
                .map(h -> h.get())
                .collect(toList());
        
        return hosts;
    }
    
    /** Discover a single host using DNS. Silently ignores DNS / IO errors.
     * @return the Host generated or none result if an error occured.
     */
    private Optional<Host> ignorantDiscover(String in) {
        try {
            MDC.put("in", in);
            if (in.isEmpty()) {
                return Optional.empty();
            }
            
            return Optional.of(discover(in));
        } catch (UnknownHostException ex) {
            log.warn(in, ex);
            return Optional.empty();
        }
        finally {
            MDC.remove("in");
        }
    }
    
    /** Discover a single host by DNS.
     * @param in a dns resolvable name.
     */
    private Host discover(String in) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(in);
        Host result = new Host();
        result.setId(UUID.randomUUID());
        result.setFqdn(address.getCanonicalHostName());
        InetAddress[] all = InetAddress.getAllByName(in);
        List<String> allIps = Stream.of(all).map(i -> i.getHostAddress()).collect(toList());
        result.setIps(allIps.toArray(new String[all.length]));
        statusLine.printf("%s -> %s", in, allIps.toString());
        int idx = in.indexOf(".");
        result.setName(idx != -1 ? in.substring(0, idx) : in);
        result.setCreatedAt(new Date());
        result.setUpdatedAt(result.getCreatedAt());
        return result;
    }
    
    /** Discover a single host by DNS.
     * @param hosts the list of hosts to update
     */
    private void update(List<Host> hosts) throws UnknownHostException {
        AtomicInteger atomicInteger = new AtomicInteger();
        hosts.stream().parallel().forEach(h -> {
            try {
                updateFqdn(h);
                updateServerAndReachability(h);
                int val = atomicInteger.addAndGet(1);
                statusLine.printf("%d/%d. %s -> %s",
                        val,
                        hosts.size(),
                        h.getName(),
                        h.getSshServerVersion());
            }
            finally {
                h.setUpdatedAt(new Date());                
            }
        });
    }

    private void updateFqdn(Host h) {
        try {
            InetAddress[] all = InetAddress.getAllByName(h.getFqdn());
            List<String> allIps = Stream.of(all).map(i -> i.getHostAddress()).collect(toList());
            h.setIps(allIps.toArray(new String[all.length]));
        } catch (UnknownHostException ex) {
            try {
                InetAddress inetAddress = InetAddress.getByName(h.getIps()[0]);
                h.setFqdn(inetAddress.getCanonicalHostName());
            } catch (UnknownHostException e2) {
                // if FQDN not resolvable, use the IP as last fallback
                log.warn("Host " + h.getName() + " not found", e2);
                h.setFqdn(h.getIps()[0]); // this sucks
            }
        }
    }

    public final static int SSH_PORT = 22;

    private void updateServerAndReachability(Host h) {
        try {
            SSHHostData sshHostData = SSHHostData.from(new InetSocketAddress(h.getFqdn(), SSH_PORT));
            h.setSshServerVersion(sshHostData.getServerId());
        } catch (IOException ex) {
            log.warn("Host "+h.getName()+" is not reachable. Disabling.", ex);
            h.setEnabled(false);
        }
    }
    
    public static void main(String[] args) throws IOException {
        Params params = Params.parse(args);
        if (params == null) {
            return;
        }

        Main main = new Main();
        Database database = Database.fromPath(params.getDb());
        if (params.isDiscover()) {
            List<Host> hosts = main.discover(params.getArguments());
            database.update(hosts);
        }
        if (params.isUpdate()) {
            List<Host> hosts = new ArrayList<>();
            hosts.addAll(database.getList());
            main.update(hosts);
            database.update(hosts);
        }
        
        database.save();
        
        if (params.getSshConfig() != null) {
            SSHConfig sshc = SSHConfig.fromPath(params.getSshConfig());
            sshc.pushOwn(database.getList(), params.isSetUser() ? Optional.of(params.getUser()) : Optional.empty());
            sshc.save();
        }
    }
}

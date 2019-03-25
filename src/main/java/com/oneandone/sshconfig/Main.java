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

import com.oneandone.sshconfig.file.SSHConfig;
import com.oneandone.sshconfig.file.Database;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import com.oneandone.sshconfig.bind.Host;
import org.slf4j.MDC;
import static java.util.stream.Collectors.toList;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * The main program.
 * @author Stephan Fuhrmann
 */
@Slf4j
public final class Main implements AutoCloseable {
    /** The default TCP port to use. */
    public static final int SSH_PORT = 22;

    /** The status line to output the current progress with. */
    private final StatusLine statusLine;

    /** The command line parameters. */
    private final Params params;

    /** Constructs an instance.
     * @param inParams the command line parameters to use.
     * */
    public Main(final Params inParams) {
        this.params = Objects.requireNonNull(inParams);
        this.statusLine = new StatusLine(System.err);
    }

    /** Discover a list of hosts by their DNS name. Will only
     * return the discovered hosts. The others will be silently
     * dropped.
     * @param discover the list of FQDNs / IPs to discover.
     * @return the list of discovered hosts.
     */
    private List<Host> discover(final List<String> discover) {
        log.debug("Discovering started for {} args", discover.size());

        return discover
                .stream()
                .parallel()
                .map(discoverMe -> ignorantDiscover(discoverMe))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    /** Discover a single host using DNS. Silently ignores DNS / IO errors.
     * @param in the FQDN / IP to try to discover.
     * @return the Host generated or {@linkplain Optional#empty() empty}
     * result if an error occured.
     */
    private Optional<Host> ignorantDiscover(final String in) {
        try {
            MDC.put("in", in);
            if (in.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(discover(in));
        } catch (UnknownHostException ex) {
            log.warn(in, ex);
            return Optional.empty();
        } finally {
            MDC.remove("in");
        }
    }

    /** Discover a single host by DNS.
     * @param in a dns resolvable name.
     * @return the discovered host entry.
     * @throws UnknownHostException if the host could not be resolved
     * by domain name service.
     */
    private Host discover(final String in) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(in);
        Host result = new Host();
        result.setId(UUID.randomUUID());
        result.setFqdn(address.getCanonicalHostName());
        InetAddress[] all = InetAddress.getAllByName(in);
        List<String> allIps = Stream.of(all)
                .map(InetAddress::getHostAddress)
                .collect(toList());
        result.setIps(allIps.toArray(new String[all.length]));
        statusLine.printf("%s -> %s", in, allIps.toString());
        int idx = in.indexOf('.');
        if (idx != -1) {
            result.setName(in.substring(0, idx));
        } else {
            result.setName(in);
        }
        result.setCreatedAt(new Date());
        result.setUpdatedAt(result.getCreatedAt());
        result.setUser(params.getUser());
        result.setGroup(params.getGroup());
        return result;
    }

    /** Discover a single host by DNS.
     * @param hosts the list of hosts to update.
     * @throws UnknownHostException if the host could not be resolved
     * by domain name service.
     */
    private void update(final List<Host> hosts) {
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
            } finally {
                h.setUpdatedAt(new Date());
            }
        });
    }

    /** Updates the fully-qualified domain name of the host.
     * @param h the host to update the FQDN of.
     */
    private void updateFqdn(final Host h) {
        try {
            InetAddress[] all = InetAddress.getAllByName(h.getFqdn());
            List<String> allIps = Stream.of(all)
                    .map(InetAddress::getHostAddress)
                    .collect(toList());
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

    /** Updates the SSH server version and the reachability information.
     * Unreachable hosts will be {@link Host#enabled disabled}.
     * @param h the host to update the data form.
     */
    private void updateServerAndReachability(final Host h) {
        try {
            SSHHostData sshHostData = SSHHostData.from(
                    new InetSocketAddress(h.getFqdn(), SSH_PORT));
            h.setSshServerVersion(sshHostData.getServerId());
        } catch (IOException ex) {
            log.warn("Host " + h.getName() + " is not reachable. Disabling.",
                    ex);
            h.setEnabled(false);
        }
    }

    /** Import a database from a {@link Params#getArguments() file}
     * or from the input stream.
     * @param database the database to update.
     * @throws IOException if the reading fails.
     * */
    private void importing(final Database database) throws IOException {
        List<Host> list;

        if (params.getArguments().isEmpty()) {
            log.debug("Importing from System.in");
            list = Database.readList(new InputStreamReader((System.in)));
        } else {
            log.debug("Importing from file {}", params.getArguments().get(0));
            try (FileReader r = new FileReader(params.getArguments().get(0))) {
                list = Database.readList(r);
            }
        }
        log.debug("Updating from {} imported hosts", list.size());
        database.update(list);
    }

    /** Export the database to the {@link Params#getArguments() file}
     * or to the output stream.
     * @param database the database to write.
     * @throws IOException if the writing fails.
     * */
    private void export(final Database database) throws IOException {
        List<Host> list;
        list = database.getList()
                    .stream()
                    .filter(h -> params.getUser() == null
                            || params.getUser().equals(h.getUser()))
                    .filter(h -> params.getGroup() == null
                            || params.getGroup().equals(h.getGroup()))
                    .collect(toList());

        if (params.getArguments().isEmpty()) {
            Database.save(new OutputStreamWriter(System.out), list);
        } else {
            try (FileWriter w = new FileWriter(params.getArguments().get(0))) {
                Database.save(w, list);
            }
        }
    }

    /** Entry point for the program.
     * @param args the command line arguments for parsing with {@link Params}.
     */
    public static void main(final String[] args) {
        Params params = Params.parse(args);
        if (params == null) {
            return;
        }

        try (Main main = new Main(params)) {
            Database database = Database.fromPath(params.getDb());
            if (params.isDiscover()) {
                List<Host> hosts = main.discover(params.getArguments());
                database.update(hosts);
                database.save();
            }
            if (params.isUpdate()) {
                List<Host> hosts = new ArrayList<>();
                hosts.addAll(database.getList());
                main.update(hosts);
                database.update(hosts);
                database.save();
            }
            if (params.isImporting()) {
                main.importing(database);
                database.save();
            }
            if (params.isExport()) {
                main.export(database);
            }

            if (params.getSshConfig() != null) {
                SSHConfig sshc = SSHConfig.fromPath(params.getSshConfig());
                sshc.pushOwn(database.getList());
                sshc.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("Got exception", e);
        }
    }

    @Override
    public void close() throws Exception {
        statusLine.close();
    }
}

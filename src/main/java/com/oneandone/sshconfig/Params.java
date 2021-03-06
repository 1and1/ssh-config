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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * Command line parameters.
 * @author Stephan Fuhrmann
 */
@Slf4j
public final class Params {

    /** Whether to show the command line help. */
    @Getter
    @Option(name = "-help", aliases = {"-h"},
            usage = "Show this command line help.", help = true)
    private boolean help;

    /** Triggers discover mode.
     * The command line hosts will be tried to be discovered. */
    @Getter
    @Option(name = "-discover", aliases = {"-d"},
            usage = "Discover hosts given in the command line using DNS.")
    private boolean discover;

    /** Update enabled hosts in the database. */
    @Getter
    @Option(name = "-update", aliases = {"-u"},
            usage = "Update enabled database hosts IP addresses (restricted to"
                    + " enabled).")
    private boolean update;

    /** Update all hosts in the database. */
    @Getter
    @Option(name = "-update-all", aliases = {"-A"},
            usage = "Update all database hosts IP addresses"
                    + " (includes disabled).")
    private boolean updateAll;

    /** Import into the database. */
    @Getter
    @Option(name = "-import", aliases = {"-i"},
            usage = "Import a database. "
                    + "Reads either from argument or stdin.")
    private boolean importing;

    /** Export the database. */
    @Getter
    @Option(name = "-export", aliases = {"-e"},
            usage = "Export the database. "
                    + "Writes to stdout or file argument. "
                    + "User and group parameters "
                    + "can be used for filtering.")
    private boolean export;

    /** The user to set for the host entries. */
    @Getter
    @Option(name = "-user", aliases = {"-U"},
            usage = "The user name to use for the entry for discovery"
                    + " or export filtering.",
            metaVar = "USER")
    private String user;

    /** The group to set for the host entries. */
    @Getter
    @Option(name = "-group", aliases = {"-G"},
            usage = "The group name to use for the entry for discovery"
                    + " or export filtering.",
            metaVar = "GROUP")
    private String group;

    /** The database file to use. */
    @Getter
    @Option(name = "-database", aliases = {"-D"},
            usage = "The database to use.", metaVar = "FILE")
    private Path db;

    /** The ssh config to write to. */
    @Getter
    @Option(name = "-sshcfg", aliases = {"-s"},
            usage = "The ssh config to update.", metaVar = "FILE")
    private Path sshConfig;

    /** Host IPs / FQDNs to update the database with.
     * @see #discover
     */
    @Getter
    @Argument
    private List<String> arguments = new ArrayList<>();

    /**
     * Parse the command line parameters.
     * @param args the command line parameters as passed to the
     * main method.
     * @return the parsed command line parameters or
     * {@code null} if the command line parameters could
     * not be parsed or the help option was displayed. If the
     * result is @{@code null}, then the program shall exit.
     */
    public static Params parse(final String[] args) {
        CmdLineParser cmdLineParser = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Args: {}", Arrays.toString(args));
            }

            Params result = new Params();
            result.db = Paths.get(System.getProperty(
                    "user.home"),
                    ".sshconfig.json");
            result.sshConfig = Paths.get(
                    System.getProperty("user.home"),
                    ".ssh",
                    "config");

            cmdLineParser = new CmdLineParser(result);
            cmdLineParser.parseArgument(args);

            if (result.help) {
                cmdLineParser.printUsage(System.err);
                return null;
            }

            return result;
        } catch (CmdLineException ex) {
            log.warn("Error in parsing", ex);
            System.err.println(ex.getMessage());
            if (cmdLineParser != null) {
                cmdLineParser.printUsage(System.err);
            }
        }
        return null;
    }
}

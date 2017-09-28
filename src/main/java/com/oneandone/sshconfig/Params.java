/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oneandone.sshconfig;

import java.io.File;
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
public class Params {
    
    @Getter
    @Option(name = "-help", aliases = {"-h"}, usage = "Show this command line help.", help = true)
    private boolean help;
    
    @Getter
    @Option(name = "-discover", aliases = {"-d"}, usage = "Discover hosts given in the command line using DNS.")
    private boolean discover;
    
    @Getter
    @Option(name = "-update", aliases = {"-u"}, usage = "Update all hosts IP addresses.")
    private boolean update;
    
    @Getter
    @Option(name = "-user", aliases = {"-U"}, usage = "The user name to use.", metaVar = "USER")
    private String user;
    
    @Getter
    @Option(name = "-set-user", aliases = {"-Z"}, usage = "Set the user name.")
    private boolean setUser = false;
    
    @Getter
    @Option(name = "-database", aliases = {"-D"}, usage = "The database to use.", metaVar = "FILE")
    private Path db;
    
    @Getter
    @Option(name = "-sshcfg", aliases = {"-s"}, usage = "The ssh config to update.", metaVar = "FILE")
    private Path sshConfig;
    
    @Getter
    @Argument
    private List<String> arguments = new ArrayList<>();
    
    public static Params parse(String[] args) {
        CmdLineParser cmdLineParser = null;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Args: {}", Arrays.toString(args));
            }
            
            Params result = new Params();
            result.user = System.getProperty("user.name");
            result.db = Paths.get(System.getProperty("user.home"),  ".sshconfig.json");
            result.sshConfig = Paths.get(System.getProperty("user.home"),  ".ssh", "config");
            
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

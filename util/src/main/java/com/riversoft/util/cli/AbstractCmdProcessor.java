/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public abstract class AbstractCmdProcessor {

    protected String cmdLineSyntax;

    public void process(String[] args) {
        Options options = getOptions();

        Option optionH = new Option("h", "help", true, "show usage information");
        optionH.setArgName(null);
        options.addOption(optionH);

        CommandLineParser cliParser = new GnuParser();
        try {
            CommandLine line = cliParser.parse(options, args);

            if (line.getOptions().length == 0 || line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.setWidth(200);
                formatter.printHelp(cmdLineSyntax, "available options", getUsageOptions(options), "");
                return;
            }

            processOptions(line);

        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @param options
     * @return
     */
    protected abstract Options getUsageOptions(Options options);

    /**
     * @param options
     */
    protected abstract void processOptions(CommandLine cmdLine);

    /**
     * @return
     */
    protected abstract Options getOptions();

}

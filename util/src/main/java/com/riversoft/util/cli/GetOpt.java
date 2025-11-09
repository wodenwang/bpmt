/*
 * $Id: GetOpt.java 304854 2012-07-05 13:13:58Z pontus $
 * 
 * $Copyright: Copyright (c) 1999 Oracle Corporation all rights reserved $
 */
package com.riversoft.util.cli;

import java.util.Vector;

/**
 * GetOpt is an implementation of a POSIX like getopt() functionality. To be used by Java main programs to parse command
 * line options.
 * <p>
 * <b>Example:</b>
 * <p>
 * <code>
 * <pre>
 * ...
 * // The usage of this program is like: <program> [ -h ] -u <user> file1, ..., fileN
 * GetOpt opt = new GetOpt(args, "hu:");
 * int c;
 * String user = null;
 * while ((c = opt.getOpt()) != -1) {
 *    switch (c) {
 *        case 'h':
 *           help();
 *           break;
 *        case 'u':
 *           user = opt.getArg();
 *           break;
 *        case '?':
 *           usage();
 *           break;
 *    }
 * }
 * if (user == null) usage();
 * // Get the rest of the arguments (non option arguments)
 * 
 * String[] files = opt.getNonOpt();
 * 
 * if (files.length == 0) usage();
 * 
 * ...
 * </code> </pre>
 * 
 * @author pelarsso
 * @since PANAMA_10
 * @version $Revision: 74 $
 */
public class GetOpt {
    private int optind;
    private String optString;
    private Option[] options;
    private String[] nonoptions;
    private String optArg;

    /**
     * The inner option class
     */
    class Option {
        int opt;
        String arg = null;

        public Option(int opt, String arg) {
            this.opt = opt;
            this.arg = arg;
        }

        public Option(int opt) {
            this.opt = opt;
        }
    }

    /** Disable empty constructor */
    @SuppressWarnings("unused")
    private GetOpt() {
    }

    /**
     * @param args
     *            the arguments.
     * @param optString
     *            the option string including qualifiers.
     */
    public GetOpt(String[] args, String optString) {
        optind = 0;
        this.optString = optString;
        parseArgs(args);
    }

    private void splitArg(String arg, Vector<Option> v) {
        int index;
        for (int i = 1; i < arg.length(); i++) {
            index = optString.indexOf(arg.charAt(i));
            v.addElement(index != -1 ? new Option(arg.charAt(i)) : new Option('?'));
        }
    }

    private void parseArgs(String[] args) {
        Vector<Option> v = new Vector<Option>();
        Vector<String> n = new Vector<String>();
        int i = 0;
        for (; i < args.length;) {
            // System.err.println("\"" + args[i] + "\"");
            if (args[i].charAt(0) == '-') {
                int len = args[i].length();
                if (len > 1) {
                    int ch = args[i].charAt(1);
                    int index = optString.indexOf(ch);
                    if (index != -1 && index < (optString.length() - 1) && optString.charAt(index + 1) == ':') {
                        String arg;
                        if (len > 2) {
                            arg = args[i].substring(2);
                        } else {
                            i++;
                            arg = (i < args.length) ? args[i] : null;
                        }
                        v.addElement(new Option(ch, arg));
                    } else {
                        splitArg(args[i], v);
                    }
                }
            } else {
                n.addElement(args[i]);
            }
            i++;
        }
        options = new Option[v.size()];
        v.copyInto(options);
        nonoptions = new String[n.size()];
        n.copyInto(nonoptions);
    }

    /**
     * @return the non option arguments.
     */
    public String[] getNonOpt() {
        return nonoptions;
    }

    /**
     * @return the next option or '?' if this option is unknown or -1 if end of options.
     */
    public int getOpt() {
        int ch = -1;
        optArg = null;
        if (optind < options.length) {
            optArg = options[optind].arg;
            ch = options[optind++].opt;
        }
        return ch;
    }

    /**
     * @return the current option argument.
     */
    public String getArg() {
        return optArg;
    }

}

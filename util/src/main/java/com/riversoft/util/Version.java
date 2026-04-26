/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.util;

/**
 * @author exizhai
 *
 */
public class Version implements Comparable<Version> {

    private int major, minor, micro;

    public Version(int major, int minor, int micro) {
        this.major = major;
        this.micro = minor;
        this.micro = micro;
    }

    public Version() {
    }

    public static Version valueOf(String version) {
        if (isValidVersion(version)) {
            String[] versionParts = version.split("\\.");

            Version v = new Version();
            v.setMajor(Integer.valueOf(versionParts[0]));
            v.setMinor(Integer.valueOf(versionParts[1]));
            v.setMicro(Integer.valueOf(versionParts[2]));

            return v;
        } else {
            throw new IllegalArgumentException("Version " + version + " is not a valid version number.");
        }
    }

    public static boolean isValidVersion(String version) {
        return true;
    }

    public static boolean isValidUpgradePath(String from, String to) {
        Version fromVersion = valueOf(from);
        Version toVersion = valueOf(to);
        return fromVersion.getMajor() == toVersion.getMajor()
                && (toVersion.getMinor() > fromVersion.getMinor() || toVersion.getMicro() > fromVersion.getMicro());
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return major;
    }

    /**
     * @param major the major to set
     */
    public void setMajor(int major) {
        this.major = major;
    }

    /**
     * @return the minor
     */
    public int getMinor() {
        return minor;
    }

    /**
     * @param minor the minor to set
     */
    public void setMinor(int minor) {
        this.minor = minor;
    }

    /**
     * @return the micro
     */
    public int getMicro() {
        return micro;
    }

    /**
     * @param micro the micro to set
     */
    public void setMicro(int micro) {
        this.micro = micro;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Version anotherVersion) {
        if (major > anotherVersion.getMajor()) {
            return 1;
        } else if (major < anotherVersion.getMajor()) {
            return -1;
        } else {
            if (minor > anotherVersion.getMinor()) {
                return 1;
            } else if (minor < anotherVersion.getMinor()) {
                return -1;
            } else {
                if (micro > anotherVersion.getMicro()) {
                    return 1;
                } else if (micro < anotherVersion.getMicro()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    @Override
    public String toString() {
        return this.getMajor() + "." + this.getMinor() + "." + this.getMicro();
    }

}

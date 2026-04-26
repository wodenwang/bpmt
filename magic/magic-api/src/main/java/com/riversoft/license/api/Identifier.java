/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.license.api;

/**
 * @author Borball
 * 
 */
public class Identifier {

    private String name;
    private String password;
    private boolean isCommercial;
    private boolean isRegister;
    private int maxSessions;
    private int level;
    private String identifier;
    private String skey;
    private String platform;
    private String version;

    /**
     * @return the maxSessions
     */
    public int getMaxSessions() {
        return maxSessions;
    }
    /**
     * @param maxSessions the maxSessions to set
     */
    public void setMaxSessions(int maxSessions) {
        this.maxSessions = maxSessions;
    }
    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the skey
     */
    public String getSkey() {
        return skey;
    }
    /**
     * @param skey the skey to set
     */
    public void setSkey(String skey) {
        this.skey = skey;
    }
    
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the isCommercial
     */
    public boolean isCommercial() {
        return isCommercial;
    }
    /**
     * @param isCommercial the isCommercial to set
     */
    public void setCommercial(boolean isCommercial) {
        this.isCommercial = isCommercial;
    }
    
    /**
     * @return the isRegister
     */
    public boolean isRegister() {
        return isRegister;
    }
    /**
     * @param isRegister the isRegister to set
     */
    public void setRegister(boolean isRegister) {
        this.isRegister = isRegister;
    }
    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }
    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }
    /**
     * @param platform the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

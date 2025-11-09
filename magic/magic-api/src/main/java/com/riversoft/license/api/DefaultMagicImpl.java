/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.license.api;

import java.util.HashSet;
import java.util.Set;

/**
 * @author exizhai
 * 
 */
public class DefaultMagicImpl implements Magic {

    Identifier identifier = new Identifier();

    public DefaultMagicImpl() {
        identifier.setRegister(false);
        identifier.setCommercial(false);
        identifier.setSkey("unknown");
        identifier.setName("unknown");
        identifier.setMaxSessions(5);
        identifier.setLevel(-1);
        identifier.setPlatform("unknown");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.riversoft.license.api.Magic#currentIdentifier()
     */
    @Override
    public Identifier currentIdentifier() {
        return identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.riversoft.license.api.Magic#isKeepCopyRight()
     */
    @Override
    public boolean isKeepCopyRight() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.riversoft.license.api.Magic#getSupportDBs()
     */
    @Override
    public Set<String> getSupportDBs() {
        Set<String> dbs = new HashSet<>();
        dbs.add("h2");
        return dbs;
    }

    /* (non-Javadoc)
     * @see com.riversoft.license.api.Magic#canAutoUpdate()
     */
    @Override
    public boolean canAutoUpdate() {
        return false;
    }

    /* (non-Javadoc)
     * @see com.riversoft.license.api.Magic#getMaxSessions()
     */
    @Override
    public int getMaxSessions() {
        return 5;
    }

}

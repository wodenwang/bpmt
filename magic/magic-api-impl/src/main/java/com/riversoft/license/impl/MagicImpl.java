/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.license.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.riversoft.util.jackson.JsonMapper;
import org.apache.commons.io.IOUtils;

import com.riversoft.license.api.Identifier;
import com.riversoft.license.api.Magic;

/**
 * @author Borball
 * 
 */
public class MagicImpl implements Magic {

    private Identifier identifier = null;

    public MagicImpl() {
        loadIdentifier();
    }

    private void loadIdentifier() {
        String file = "/META-INF/identifier.json";
        URL fileURL = this.getClass().getResource(file);
        if (fileURL != null) {
            try {
                InputStream is = fileURL.openStream();
                int size = is.available();
                byte[] buffer = new byte[size];
                
                IOUtils.readFully(fileURL.openStream(), buffer);

                //TODO:JSON
//                JsonConfig jsonConfig = new JsonConfig();
//                jsonConfig.setExcludes(new String[]{"active", "role", "comments", "cas"});
                identifier = JsonMapper.defaultMapper().fromJson(new String(buffer), Identifier.class);
                
            } catch (IOException e) {
                
            }
        }
    }

    @Override
    public Identifier currentIdentifier() {
        return identifier;
    }

    @Override
    public boolean isKeepCopyRight() {
        return !identifier.isCommercial();
    }

    @Override
    public Set<String> getSupportDBs() {
        Set<String> dbs = new HashSet<>();
        dbs.add("h2");
        dbs.add("mysql");
        dbs.add("postgresql");
        dbs.add("oracle");
        dbs.add("sqlserver2005");
        dbs.add("sqlserver2008");
        dbs.add("sybase");
        return dbs;
    }

    @Override
    public int getMaxSessions() {
        return identifier.getMaxSessions();
    }

    @Override
    public boolean canAutoUpdate() {
        return identifier.isCommercial();
    }

}

/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.license.api;

import java.util.Set;

/**
 * @author Borball
 * 
 */
public interface Magic {

    Identifier currentIdentifier();

    boolean isKeepCopyRight();
    
    Set<String> getSupportDBs();
    
    int getMaxSessions();
    
    boolean canAutoUpdate();
    
}

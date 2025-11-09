/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2012 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.script;

import java.util.Set;

/**
 * @author Borball
 * 
 */
public class User {

    int id;
    String name;
    String mail;
    Set<Role> roles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}

/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.dbtool.export;

/**
 * @author Borball
 */
public class ImportExportResponse {

    private boolean success;
    private String detailes;

    public ImportExportResponse(boolean success, String details) {
        this.success = success;
        this.detailes = details;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the detailes
     */
    public String getDetailes() {
        return detailes;
    }

    /**
     * @param detailes the detailes to set
     */
    public void setDetailes(String detailes) {
        this.detailes = detailes;
    }

}

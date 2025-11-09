/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.generator;

/**
 * @author Borball
 * 
 */
public class Settings {

    private String module;
    private String author;
    private String outdir;
    private String hbmName;
    private String extHbmName;
    private String pagePath;
    private boolean hasMain;
    private boolean hasList;
    private boolean hasForm;
    private boolean hasDetail;
    private boolean hasBatch;
    
    /**
     * @return the hasPage
     */
    public boolean isHasPage() {
        return hasMain || hasList || hasForm || hasDetail || hasBatch;
    }

    /**
     * @return the hasMain
     */
    public boolean isHasMain() {
        return hasMain;
    }

    /**
     * @return the hasList
     */
    public boolean isHasList() {
        return hasList;
    }

    /**
     * @return the hasForm
     */
    public boolean isHasForm() {
        return hasForm;
    }

    /**
     * @return the hasDetail
     */
    public boolean isHasDetail() {
        return hasDetail;
    }

    /**
     * @return the hasBatch
     */
    public boolean isHasBatch() {
        return hasBatch;
    }

    private boolean isListOverwrite;

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the module
     */
    public String getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(String module) {
        this.module = module;
    }

    /**
     * @return the outdir
     */
    public String getOutdir() {
        return outdir;
    }

    /**
     * @param outdir the outdir to set
     */
    public void setOutdir(String outdir) {
        this.outdir = outdir;
    }

    /**
     * @return the hbmName
     */
    public String getHbmName() {
        return hbmName;
    }

    /**
     * @param hbmName the hbmName to set
     */
    public void setHbmName(String hbmName) {
        this.hbmName = hbmName;
    }

    /**
     * @return the extHbmName
     */
    public String getExtHbmName() {
        return extHbmName;
    }

    /**
     * @param extHbmName the extHbmName to set
     */
    public void setExtHbmName(String extHbmName) {
        this.extHbmName = extHbmName;
    }

    /**
     * @return the pagePath
     */
    public String getPagePath() {
        return pagePath;
    }

    /**
     * @param pagePath the pagePath to set
     */
    public void setPagePath(String pagePath) {
        this.pagePath = pagePath;
    }

    /**
     * @param hasMain the hasMain to set
     */
    public void setHasMain(boolean hasMain) {
        this.hasMain = hasMain;
    }

    /**
     * @param hasList the hasList to set
     */
    public void setHasList(boolean hasList) {
        this.hasList = hasList;
    }

    /**
     * @param hasForm the hasForm to set
     */
    public void setHasForm(boolean hasForm) {
        this.hasForm = hasForm;
    }

    /**
     * @param hasDetail the hasDetail to set
     */
    public void setHasDetail(boolean hasDetail) {
        this.hasDetail = hasDetail;
    }

    /**
     * @param hasBatch the hasBatch to set
     */
    public void setHasBatch(boolean hasBatch) {
        this.hasBatch = hasBatch;
    }

    /**
     * @return the isListOverwrite
     */
    public boolean isListOverwrite() {
        return isListOverwrite;
    }

    /**
     * @param isListOverwrite the isListOverwrite to set
     */
    public void setListOverwrite(boolean isListOverwrite) {
        this.isListOverwrite = isListOverwrite;
    }

}

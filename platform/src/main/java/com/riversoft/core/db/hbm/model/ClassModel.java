/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db.hbm.model;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Woden
 * 
 */
@XStreamAlias("class")
class ClassModel {

    @XStreamAlias("cache")
    private CacheModel cache;

    private String comment;

    @XStreamAlias("composite-id")
    private CompositeIdModel compositeId;

    @XStreamAlias("entity-name")
    @XStreamAsAttribute
    private String entityName;

    private IdModel id;

    @XStreamImplicit(itemFieldName = "map")
    private List<MapModel> maps;

    @XStreamImplicit(itemFieldName = "set")
    private List<SetModel> sets;

    @SuppressWarnings("rawtypes")
    @XStreamAsAttribute
    private Class name;

    @XStreamImplicit(itemFieldName = "property")
    private List<PropertyModel> properties;

    @XStreamImplicit(itemFieldName = "many-to-one")
    private List<ManyToOneModel> manyToOneList;

    @XStreamAsAttribute
    private String table;

    /**
     * @return the sets
     */
    public List<SetModel> getSets() {
        return sets;
    }

    /**
     * @param sets the sets to set
     */
    public void setSets(List<SetModel> sets) {
        this.sets = sets;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the compositeId
     */
    public CompositeIdModel getCompositeId() {
        return compositeId;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the id
     */
    public IdModel getId() {
        return id;
    }

    /**
     * @return the maps
     */
    public List<MapModel> getMaps() {
        return maps;
    }

    /**
     * @return the name
     */
    @SuppressWarnings("rawtypes")
    public Class getName() {
        return name;
    }

    /**
     * @return the properties
     */
    public List<PropertyModel> getProperties() {
        return properties;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @param compositeId the compositeId to set
     */
    public void setCompositeId(CompositeIdModel compositeId) {
        this.compositeId = compositeId;
    }

    /**
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @param id the id to set
     */
    public void setId(IdModel id) {
        this.id = id;
    }

    /**
     * @param maps the maps to set
     */
    public void setMaps(List<MapModel> maps) {
        this.maps = maps;
    }

    /**
     * @param name the name to set
     */
    @SuppressWarnings("rawtypes")
    public void setName(Class name) {
        this.name = name;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<PropertyModel> properties) {
        this.properties = properties;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the manyToOneList
     */
    public List<ManyToOneModel> getManyToOneList() {
        return manyToOneList;
    }

    /**
     * @param manyToOneList the manyToOneList to set
     */
    public void setManyToOneList(List<ManyToOneModel> manyToOneList) {
        this.manyToOneList = manyToOneList;
    }

}

@XStreamAlias("composite-id")
class CompositeIdModel {

    private GeneratorModel generator;

    @XStreamImplicit(itemFieldName = "key-property")
    private List<KeyPropertyModel> keyProperties;

    /**
     * @return the generator
     */
    public GeneratorModel getGenerator() {
        return generator;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(GeneratorModel generator) {
        this.generator = generator;
    }

    /**
     * @return the keyProperties
     */
    public List<KeyPropertyModel> getKeyProperties() {
        return keyProperties;
    }

    /**
     * @param keyProperties the keyProperties to set
     */
    public void setKeyProperties(List<KeyPropertyModel> keyProperties) {
        this.keyProperties = keyProperties;
    }
}

@XStreamAlias("generator")
class GeneratorModel {

    @XStreamAlias("class")
    @XStreamAsAttribute
    private String className;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }
}

@XStreamAlias("id")
class IdModel {
    private ColumnModel column;

    private GeneratorModel generator;

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @SuppressWarnings("rawtypes")
    @XStreamAlias("type")
    @XStreamAsAttribute
    private Class type;

    /**
     * @return the column
     */
    public ColumnModel getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(ColumnModel column) {
        this.column = column;
    }

    /**
     * @return the generator
     */
    public GeneratorModel getGenerator() {
        return generator;
    }

    /**
     * @param generator the generator to set
     */
    public void setGenerator(GeneratorModel generator) {
        this.generator = generator;
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
     * @return the type
     */
    @SuppressWarnings("rawtypes")
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @SuppressWarnings("rawtypes")
    public void setType(Class type) {
        this.type = type;
    }

}

@XStreamAlias("key-property")
class KeyPropertyModel {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @SuppressWarnings("rawtypes")
    @XStreamAlias("type")
    @XStreamAsAttribute
    private Class type;

    private ColumnModel column;

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
     * @return the type
     */
    @SuppressWarnings("rawtypes")
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    @SuppressWarnings("rawtypes")
    public void setType(Class type) {
        this.type = type;
    }

    /**
     * @return the column
     */
    public ColumnModel getColumn() {
        return column;
    }

    /**
     * @param column the column to set
     */
    public void setColumn(ColumnModel column) {
        this.column = column;
    }

}

@XStreamAlias("map")
class MapModel {

    @XStreamAlias("cache")
    private CacheModel cache;

    @XStreamAlias("index")
    static class IndexModel {

    }

    @XStreamAlias("key")
    static class KeyModel {

    }

    @XStreamAlias("one-to-many")
    static class OneToManyModel {

    }

    private String comment;

    private IndexModel index;

    private KeyModel key;

    @XStreamAlias("one-to-many")
    private OneToManyModel oneToMany;

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the index
     */
    public IndexModel getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(IndexModel index) {
        this.index = index;
    }

    /**
     * @return the key
     */
    public KeyModel getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(KeyModel key) {
        this.key = key;
    }

    /**
     * @return the oneToMany
     */
    public OneToManyModel getOneToMany() {
        return oneToMany;
    }

    /**
     * @param oneToMany the oneToMany to set
     */
    public void setOneToMany(OneToManyModel oneToMany) {
        this.oneToMany = oneToMany;
    }

}

@XStreamAlias("set")
class SetModel {

    @XStreamAlias("cache")
    private CacheModel cache;

    @XStreamAlias("key")
    static class KeyModel {

        @XStreamAlias("column")
        private ColumnModel column;

        /**
         * @return the column
         */
        public ColumnModel getColumn() {
            return column;
        }

        /**
         * @param column the column to set
         */
        public void setColumn(ColumnModel column) {
            this.column = column;
        }

    }

    @XStreamAlias("one-to-many")
    static class OneToManyModel {

    }

    static class ManyToManyModel {

    }

    private String comment;

    private KeyModel key;

    @XStreamAlias("one-to-many")
    private OneToManyModel oneToMany;

    @XStreamAlias("many-to-many")
    private ManyToManyModel manyToMany;

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the key
     */
    public KeyModel getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(KeyModel key) {
        this.key = key;
    }

    /**
     * @return the oneToMany
     */
    public OneToManyModel getOneToMany() {
        return oneToMany;
    }

    /**
     * @param oneToMany the oneToMany to set
     */
    public void setOneToMany(OneToManyModel oneToMany) {
        this.oneToMany = oneToMany;
    }

}

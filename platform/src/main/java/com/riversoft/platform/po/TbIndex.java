package com.riversoft.platform.po;

import org.jumpmind.db.model.IIndex;
import org.jumpmind.db.model.NonUniqueIndex;
import org.jumpmind.db.model.UniqueIndex;

import java.io.Serializable;
import java.util.Set;

/**
 * @borball on 4/17/2016.
 */
public class TbIndex implements Serializable {

    private String tableName;

    private String name;

    private Set<TbIndexedColumn> indexedColumns;

    private String description;

    private boolean unique;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<TbIndexedColumn> getIndexedColumns() {
        return indexedColumns;
    }

    public void setIndexedColumns(Set<TbIndexedColumn> indexedColumns) {
        this.indexedColumns = indexedColumns;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public IIndex build(){
        IIndex index = null;
        if(unique) {
            index = new UniqueIndex();
        } else {
            index = new NonUniqueIndex();
        }
        index.setName(name);
        for(TbIndexedColumn indexedColumn : indexedColumns) {
            index.addColumn(indexedColumn);
        }

        return index;
    }

}

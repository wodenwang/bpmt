package com.riversoft.platform.po;

import org.jumpmind.db.model.IndexColumn;

/**
 * @borball on 4/18/2016.
 */
public class TbIndexedColumn extends IndexColumn {

    private String tableName;
    private String indexName;

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

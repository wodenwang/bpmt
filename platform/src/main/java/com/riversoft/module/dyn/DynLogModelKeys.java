package com.riversoft.module.dyn;

import com.riversoft.platform.db.model.ModelKey;
import com.riversoft.platform.po.TbColumn;

import java.sql.Types;

/**
 * Created by exizhai on 05/02/2015.
 */
public enum DynLogModelKeys implements ModelKey {

    /**
     *
     */
    LOG_ID("自动ID", true, true, true, Types.BIGINT, 14, 0),

    /**
     *
     */
    TABLE_NAME("主表表名", false, false, true, Types.VARCHAR, 100, 0),

    /**
     *
     */
    BATCH_ID("批次ID", false, false, true, Types.BIGINT, 14, 0),

    /**
     *
     */
    OPR_UID("处理人", false, false, true, Types.VARCHAR, 100, 0),

    /**
     *
     */
    OPR_TYPE("操作类型", false, false, false, Types.INTEGER, 8, 0),

    /**
     *
     */
    OPR_TIME("操作时间", false, false, false, Types.TIMESTAMP, 0, 0),

    /**
     *
     */
    FIELD_VAL("字段", false, false, false, Types.VARCHAR, 100, 0),

    /**
     *
     */
    FIELD_DISPLAY("字段(展示)", false, false, false, Types.VARCHAR, 100, 0),

    /**
     *
     */
    OLD_VAL("旧值", false, false, false, Types.CLOB, 0, 0),

    /**
     *
     */
    OLD_DISPLAY("旧值(展示)", false, false, false, Types.CLOB, 0, 0),

    /**
     *
     */
    NEW_VAL("新值", false, false, false, Types.CLOB, 0, 0),

    /**
     *
     */
    NEW_DISPLAY("新值(展示)", false, false, false, Types.CLOB, 0, 0),

    ;


    private TbColumn column;

    private DynLogModelKeys(String description, boolean primaryKey, boolean autoIncrement, boolean required,
                                  int mappedTypeCode, int totalSize, int scale) {
        column = new TbColumn();
        column.setName(this.name());
        column.setPrimaryKey(primaryKey);
        column.setRequired(required);
        column.setAutoIncrement(autoIncrement);
        column.setDescription(description);
        column.setMappedTypeCode(mappedTypeCode);
        column.setTotalSize(totalSize);
        column.setScale(scale);
        column.setSort(0);
    }

    @Override
    public TbColumn getColumn() {
        return column;
    }

}

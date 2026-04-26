package com.riversoft.core.db.dialect;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by exizhai on 11/9/2015.
 */
public class OracleColumnMapRowMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map mapOfColValues = this.createColumnMap(columnCount);

        for(int i = 1; i <= columnCount; ++i) {
            String key = this.getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
            Object obj = this.getColumnValue(rs, i);
            mapOfColValues.put(key, obj);
        }

        return mapOfColValues;
    }

    protected Map<String, Object> createColumnMap(int columnCount) {
        return new LinkedCaseInsensitiveMap(columnCount);
    }

    protected String getColumnKey(String columnName) {
        return columnName;
    }

    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        if(rs.getObject(index) == null)
            return null;

        Object result = rs.getObject(index);
        if(isDoubleOrFloat(result)) {
            BigDecimal bigDecimal = (BigDecimal) result;
            return bigDecimal.doubleValue();
        } else {
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int precision = getPrecision(index, resultSetMetaData);
            if(isBigDecimal(resultSetMetaData.getColumnClassName(index))) {
                if(resultSetMetaData.getScale(index) == 0) {
                    if(resultSetMetaData.isAutoIncrement(index)) {
                        return rs.getLong(index);
                    }

                    if(isInt(precision)) {
                        return rs.getInt(index);
                    }
                    if(isLong(precision)) {
                        return rs.getLong(index);
                    }
                }
            }
        }

        return JdbcUtils.getResultSetValue(rs, index);
    }

    private boolean isDoubleOrFloat(Object result) {
        boolean isBigDecimal = result instanceof BigDecimal;
        if(isBigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) result;
            return bigDecimal.scale() > 0;
        }
        return false;
    }

    private int getPrecision(int index, ResultSetMetaData resultSetMetaData) throws SQLException {
        return resultSetMetaData.getPrecision(index);
    }

    private boolean isLong(int precision) {
        return precision > 8 && precision <= 16;
    }

    private boolean isInt(int precision) {
        return precision <= 8;
    }

    private boolean isBigDecimal(String columnClassName) {
        return "java.math.BigDecimal".equalsIgnoreCase(columnClassName);
    }
}

/**
 * 
 */
package com.riversoft.core.db.po;

import java.io.Serializable;

/**
 * 扩展属性基类
 * 
 * @author Woden
 * 
 */
@SuppressWarnings("serial")
public abstract class BaseVarItem implements Serializable {
    private String fieldKey;
    private Long valueInteger;
    private java.util.Date valueDate;
    private java.math.BigDecimal valueFloat;
    private String valueString;
    /**
     * @return the fieldKey
     */
    public String getFieldKey() {
        return fieldKey;
    }
    /**
     * @param fieldKey the fieldKey to set
     */
    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }
    /**
     * @return the valueInteger
     */
    public Long getValueInteger() {
        return valueInteger;
    }
    /**
     * @param valueInteger the valueInteger to set
     */
    public void setValueInteger(Long valueInteger) {
        this.valueInteger = valueInteger;
    }
    /**
     * @return the valueDate
     */
    public java.util.Date getValueDate() {
        return valueDate;
    }
    /**
     * @param valueDate the valueDate to set
     */
    public void setValueDate(java.util.Date valueDate) {
        this.valueDate = valueDate;
    }
    /**
     * @return the valueFloat
     */
    public java.math.BigDecimal getValueFloat() {
        return valueFloat;
    }
    /**
     * @param valueFloat the valueFloat to set
     */
    public void setValueFloat(java.math.BigDecimal valueFloat) {
        this.valueFloat = valueFloat;
    }
    /**
     * @return the valueString
     */
    public String getValueString() {
        return valueString;
    }
    /**
     * @param valueString the valueString to set
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }
}

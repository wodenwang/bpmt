/*
 * File Name  :InvokeResult.java
 * Create Date:2012-11-3 下午5:08:12
 * Author     :woden
 */

package com.riversoft.core.db;

import java.io.Serializable;
import java.util.List;

/**
 * 分页包装对象.<br>
 * 可序列化.
 * 
 */
@SuppressWarnings("serial")
public class DataPackage implements Serializable {

    private int limit;// 每页展示数量
    private List<?> list;// 记录集
    private int start;// 开始记录位置
    private long totalRecord; // 总记录数

    /**
     * 当前页面
     * 
     * @return
     */
    public int getCurrentPage() {
        if (this.limit == 0) {
            return 1;
        }

        return (this.start / this.limit) + 1;
    }

    public int getLimit() {
        return this.limit;
    }

    public List<?> getList() {
        return this.list;
    }

    public int getStart() {
        return this.start;
    }

    /**
     * 总页数
     * 
     * @return
     */
    public long getTotalPage() {
        return ((this.totalRecord - 1) / this.limit) + 1;
    }

    public long getTotalRecord() {
        return this.totalRecord;
    }

    /**
     * 是否有分页
     * 
     * @return
     */
    public boolean isHasPage() {
        if (this.totalRecord == 0 && this.list != null && this.list.size() > 0) {
            return false;
        }

        return true;
    }

    /**
     * 设置-1代表无限大;设置0代表采用默认值
     * 
     * @param limit
     */
    public void setLimit(int limit) {
        if (limit > 0) {
            this.limit = limit;
        }

        if (limit < 0) {
            this.limit = 5000;// 无限制
        }
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setTotalRecord(long totalRecord) {
        this.totalRecord = totalRecord;
    }
}

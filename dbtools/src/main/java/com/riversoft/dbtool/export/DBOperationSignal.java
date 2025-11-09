package com.riversoft.dbtool.export;

/**
 * Created by Borball on 2/10/14.
 */
public interface DBOperationSignal {

    // 操作开始
    public void begin();

    // 操作进行中,仅允许在循环中使用
    public void signal(String table, String description);

    // 操作结束
    public void end();

}

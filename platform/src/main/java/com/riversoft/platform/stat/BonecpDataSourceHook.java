package com.riversoft.platform.stat;

import com.jolbox.bonecp.ConnectionHandle;
import com.jolbox.bonecp.PoolUtil;
import com.jolbox.bonecp.StatementHandle;
import com.jolbox.bonecp.hooks.AcquireFailConfig;
import com.jolbox.bonecp.hooks.ConnectionHook;
import com.jolbox.bonecp.hooks.ConnectionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * Created by exizhai on 3/18/14.
 */
public class BonecpDataSourceHook implements ConnectionHook {

    private static Logger logger = LoggerFactory.getLogger("sql.logger");

    @Override
    public void onAcquire(ConnectionHandle connection) {

    }

    @Override
    public void onCheckIn(ConnectionHandle connection) {

    }

    @Override
    public void onCheckOut(ConnectionHandle connection) {

    }

    @Override
    public void onDestroy(ConnectionHandle connection) {

    }

    @Override
    public boolean onAcquireFail(Throwable t, AcquireFailConfig acquireConfig) {
        return false;
    }

    @Override
    public void onQueryExecuteTimeLimitExceeded(ConnectionHandle connectionHandle, Statement statement, String sql,
            Map<Object, Object> params, long l) {
        logSqlTime(sql, params, String.valueOf(l));
    }

    @Override
    public void onQueryExecuteTimeLimitExceeded(ConnectionHandle conn, Statement statement, String sql,
            Map<Object, Object> logParams) {
    }

    @Override
    public void onQueryExecuteTimeLimitExceeded(String sql, Map<Object, Object> logParams) {

    }

    @Override
    public void onBeforeStatementExecute(ConnectionHandle conn, StatementHandle statement, String sql,
            Map<Object, Object> params) {
    }

    @Override
    public void onAfterStatementExecute(ConnectionHandle conn, StatementHandle statement, String sql,
            Map<Object, Object> params) {
    }

    private void logSqlTime(String sql, Map<Object, Object> params, String time) {
        if (shouldRecord(sql)) {
            MDC.put("time", time);
            MDC.put("sql", PoolUtil.fillLogParams(sql, params).toLowerCase());
            logger.info("");
        }
    }

    private boolean shouldRecord(String sql) {
        return isSelectSQL(sql) && !isIgnored(sql);
    }

    private boolean isIgnored(String sql) {
        // TODO: add ignore list
        return false;
    }

    private boolean isSelectSQL(String sql) {
        return sql.toLowerCase().contains("select");
    }

    @Override
    public boolean onConnectionException(ConnectionHandle connection, String state, Throwable t) {
        return false;
    }

    @Override
    public ConnectionState onMarkPossiblyBroken(ConnectionHandle paramConnectionHandle, String paramString,
            SQLException paramSQLException) {
        return ConnectionState.NOP;
    }
}

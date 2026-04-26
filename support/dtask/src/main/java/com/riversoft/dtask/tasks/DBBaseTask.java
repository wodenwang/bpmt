package com.riversoft.dtask.tasks;

import org.apache.tools.ant.BuildException;
import org.jumpmind.db.model.Database;
import org.jumpmind.db.platform.IDatabasePlatform;
import org.jumpmind.db.platform.JdbcDatabasePlatformFactory;
import org.jumpmind.db.sql.SqlTemplateSettings;

import javax.sql.DataSource;

/**
 * Abstract DB related task
 * Created by exizhai on 2/11/14.
 */
public abstract class DBBaseTask extends BaseRiverTask {

    protected DataSource dataSource;
    protected Database database;
    private String jdbcConf;

    public void setJdbcConf(String jdbcConf) {
        this.jdbcConf = jdbcConf;
    }

    @Override
    protected void doExecute() throws BuildException {
        init0();

        dbOperation();
    }

    public abstract void dbOperation();

    private void init0() {
        try {
            dataSource = DataSourceInstance.getInstance(jdbcConf).getDataSource();
            IDatabasePlatform databasePlatform = JdbcDatabasePlatformFactory.createNewPlatformInstance(dataSource, new SqlTemplateSettings(), true);
            database = databasePlatform.readDatabase(databasePlatform.getDefaultCatalog(), databasePlatform.getDefaultSchema(), new String[]{});
        } catch (Exception e) {
            throw new BuildException("init0 failed:" + e);
        }
    }


    protected String buildJDBCURL(String h2FileName) {
        return "jdbc:h2:" + h2FileName + ";AUTO_SERVER=TRUE;DATABASE_TO_UPPER=false";
    }

    protected String trimH2Ext(String h2Path) {
        if (h2Path.endsWith(".h2.db")) {
            return h2Path.substring(0, h2Path.length() - 6);
        } else {
            return h2Path;
        }
    }

}

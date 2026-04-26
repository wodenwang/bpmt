package com.riversoft.dtask.tasks;

import com.riversoft.dbtool.export.Importer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A task to handle db import
 * Created by exizhai on 2/11/14.
 */
public class ImportTask extends DBBaseTask {

    private Logger logger = LoggerFactory.getLogger("ImportTask");
    private boolean exitIfError;
    private boolean clearBeforeImport;
    private boolean replaceIfConflict;

    private String excelPath;

    public void setExcelPath(String excelPath) {
        this.excelPath = excelPath;
    }

    public void setExitIfError(String exitIfError) {
        this.exitIfError = Boolean.valueOf(exitIfError);
    }

    public void setClearBeforeImport(String clearBeforeImport) {
        this.clearBeforeImport = Boolean.valueOf(clearBeforeImport);
    }

    public void setReplaceIfConflict(String replaceIfConflict) {
        this.replaceIfConflict = Boolean.valueOf(replaceIfConflict);
    }

    @Override
    public void dbOperation() {
        Importer importer = new Importer(dataSource);

        try {
            logger.info("准备开始导入文件:" + excelPath + "到数据库.");
            importer.doImport(new File(excelPath), clearBeforeImport, replaceIfConflict, exitIfError);
        } catch (Exception e) {
            logger.error("导入失败:", e);
        }
    }
}

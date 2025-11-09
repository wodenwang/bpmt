package com.riversoft.core.db.dialect;

import com.riversoft.core.Config;

/**
 * Created by exizhai on 11/11/2015.
 */
public class DatabaseMeta {

    public static boolean isOracle(){
        return "com.riversoft.core.db.dialect.Oracle10gDialectFix".equals(Config.get("hibernate.dialect", ""));
    }

}

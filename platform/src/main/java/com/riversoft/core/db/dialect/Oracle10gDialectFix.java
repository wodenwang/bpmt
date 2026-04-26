package com.riversoft.core.db.dialect;

import org.hibernate.dialect.Oracle10gDialect;

import java.sql.Types;

/**
 * Created by exizhai on 15/03/2015.
 */
public class Oracle10gDialectFix extends Oracle10gDialect {

	public Oracle10gDialectFix() {
		super();
		registerColumnType(Types.LONGVARCHAR, "clob");
		registerColumnType(Types.LONGNVARCHAR, "clob");
	}

}

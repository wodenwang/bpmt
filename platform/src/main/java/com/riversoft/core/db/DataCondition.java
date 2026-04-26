/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.core.db;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 查询条件，生成queryMap.<br>
 * 
 * <pre>
 * +----------+---------------+-------------+--------------------------------------------------------------+
 * +   type   +   directions  +   command   +                          note                                +
 * +----------+---------------+-------------+--------------------------------------------------------------+
 * +          +               + sisn        + is null.1:null;0:not null.                                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + sn          + null.                                                        +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + snn         + not null.                                                    +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + sl          + like." where key like '%value%' ".                           +
 * +          +               +-------------+--------------------------------------------------------------+
 * +    s     +     string    + snl         + not like." where key not like '%value%' ".                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + se          + =." where key = 'value' ".                                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + sne         + !=." where key != 'value' ".                                 +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + sin         + in." where key in ('val1','val2') ".                         + 
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + snin         + not in." where key not in ('val1','val2') ".                + 
 * +----------+---------------+-------------+--------------------------------------------------------------+
 * +          +               + nisn        + is null.1:null;0:not null.                                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nn          + null.                                                        +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nnn         + not null.                                                    +
 * +          +               +-------------+--------------------------------------------------------------+
 * +    n     +     number    + ne          + =." where key = value ".                                     +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nne         + !=." where key != value ".                                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nb          + >." where key > value ".                                     +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nbe         + >=." where key >= value ".                                   + 
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + ns          + <." where key < value ".                                     + 
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nse         + <=." where key <= value ".                                   + 
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nin         + in." where key in (val1,val2) ".                             + 
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + nnin        + not in." where key not in (val1,val2) ".                     + 
 * +----------+---------------+-------------+--------------------------------------------------------------+
 * +          +               + disn        + is null.1:null;0:not null.                                   +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + dn          + null.                                                        +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + dnn         + not null.                                                    +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + dnm         + <= .                                                         +
 * +          +               +-------------+--------------------------------------------------------------+
 * +    d     +    datetime   + dnl         + >= .                                                         +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + de          + = .                                                          +
 * +          +               +-------------+--------------------------------------------------------------+
 * +          +               + dne         + != .                                                         +
 * +----------+---------------+-------------+--------------------------------------------------------------+
 * </pre>
 * 
 * @author Woden
 */
public class DataCondition {
	private Map<String, Object> queryMap;
	private transient AtomicInteger sec = new AtomicInteger();

	public DataCondition() {
		this.queryMap = new LinkedHashMap<>();
	}

	public DataCondition(Map<String, Object> queryMap) {
		this.queryMap = queryMap;
	}

	/**
	 * 直接设置条件
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public DataCondition addCondition(String key, Object value) {
		queryMap.put(key, value);
		return this;
	}

	/**
	 * 自定义sql/hql语句拼接
	 * 
	 * @param sql
	 * @return
	 */
	public DataCondition addSql(String sql) {
		queryMap.put("_sql_" + sec.getAndIncrement(), sql);
		return this;
	}

	/**
	 * <b>field</b> is null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setStringIsNull(String field) {
		queryMap.put("_sisn_" + field, "0");
		return this;
	}

	/**
	 * <b>field</b> is not null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setStringIsNotNull(String field) {
		queryMap.put("_sisn_" + field, "1");
		return this;
	}

	/**
	 * <b>field</b> like '%<b>value</b>%'
	 * 
	 * @param field
	 * @param value
	 */
	public DataCondition setStringLike(String field, String value) {
		queryMap.put("_sl_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> not like '%<b>value</b>%'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setStringNotLike(String field, String value) {
		queryMap.put("_snl_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> = '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setStringEqual(String field, String value) {
		queryMap.put("_se_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> != '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setStringNotEqual(String field, String value) {
		queryMap.put("_sne_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> in ('<b>value1</b>','<b>value2</b>')
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setStringIn(String field, String... value) {
		queryMap.put("_sin_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> not in ('<b>value1</b>','<b>value2</b>')
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setStringNotIn(String field, String... value) {
		queryMap.put("_snin_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> is null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setNumberIsNull(String field) {
		queryMap.put("_nisn_" + field, "0");
		return this;
	}

	/**
	 * <b>field</b> is not null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setNumberIsNotNull(String field) {
		queryMap.put("_nisn_" + field, "1");
		return this;
	}

	/**
	 * <b>field</b> = <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberEqual(String field, String value) {
		queryMap.put("_ne_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> != <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberNotEqual(String field, String value) {
		queryMap.put("_nne_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> > <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberBig(String field, String value) {
		queryMap.put("_nb_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> < <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberSmall(String field, String value) {
		queryMap.put("_ns_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> >= <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberBigEqual(String field, String value) {
		queryMap.put("_nbe_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> <= <b>value</b>
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberSmallEqual(String field, String value) {
		queryMap.put("_nse_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> in (<b>value1</b>,<b>value2</b>)
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberIn(String field, String... value) {
		queryMap.put("_nin_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> not in (<b>value1</b>,<b>value2</b>)
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setNumberNotIn(String field, String... value) {
		queryMap.put("_nnin_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> is null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setDateIsNull(String field) {
		queryMap.put("_disn_" + field, "0");
		return this;
	}

	/**
	 * <b>field</b> is not null
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setDateIsNotNull(String field) {
		queryMap.put("_disn_" + field, "1");
		return this;
	}

	/**
	 * <b>field</b> >= '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setDateBigEqual(String field, String value) {
		queryMap.put("_dnl_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> <= '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setDateSmallEqual(String field, String value) {
		queryMap.put("_dnm_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> = '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setDateEqual(String field, String value) {
		queryMap.put("_de_" + field, value);
		return this;
	}

	/**
	 * <b>field</b> != '<b>value</b>'
	 * 
	 * @param field
	 * @param value
	 * @return
	 */
	public DataCondition setDateNotEqual(String field, String value) {
		queryMap.put("_dne_" + field, value);
		return this;
	}

	/**
	 * order by <b>field</b> asc
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setOrderByAsc(String field) {
		queryMap.put("_orderby_" + field, "asc");
		return this;
	}

	/**
	 * order by <b>field</b> desc
	 * 
	 * @param field
	 * @return
	 */
	public DataCondition setOrderByDesc(String field) {
		queryMap.put("_orderby_" + field, "desc");
		return this;
	}

	/**
	 * order by <b>field</b> <b>dir</b>
	 * 
	 * @param field
	 * @param dir
	 * @return
	 */
	public DataCondition setOrderBy(String field, String dir) {
		if (field != null && !field.equals("")) {
			queryMap.put("_orderby_" + field, dir);
		}
		return this;
	}

	/**
	 * order by <b>sql</b>
	 * 
	 * @param sql
	 * @return
	 */
	public DataCondition setOrderBySQL(String sql) {
		queryMap.put("_orderbysql_" + sec.getAndIncrement(), sql);
		return this;
	}

	/**
	 * 转换成{@link QueryStringBuilder}需要的查询条件.
	 * 
	 * @return
	 */
	public final Map<String, ?> toEntity() {
		return queryMap;
	}
}
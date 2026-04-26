/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.script;

import com.riversoft.core.db.po.Code2NameVO;

/**
 * 系统允许的脚本类型
 * 
 * @author Woden
 * 
 */
public enum ScriptTypes implements Code2NameVO {

	GROOVY(1, "Groovy(推荐)"), EL(2, "EL");

	private Integer code;
	private String showName;

	private ScriptTypes(Integer code, String showName) {
		this.code = code;
		this.showName = showName;
	}

	@Override
	public Object getCode() {
		return code;
	}

	@Override
	public String getShowName() {
		return showName;
	}

	public static ScriptTypes forCode(Integer code) {
		for (ScriptTypes types : values()) {
			if (types.code.equals(code)) {
				return types;
			}
		}

		// 默认值
		return GROOVY;
	}

}

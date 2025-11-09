/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.platform.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.riversoft.core.BeanFactory;
import com.riversoft.core.db.ORMService;
import com.riversoft.core.db.po.Code2NameTreeVO;
import com.riversoft.core.db.po.Code2NameVO;

/**
 * 基础数据服务类
 * 
 * @author Woden
 * 
 */
public class BaseDataService {

    /**
     * 获取单例
     * 
     * @return
     */
    public static BaseDataService getInstance() {
        BaseDataService service = BeanFactory.getInstance().getSingleBean(BaseDataService.class);
        return service;
    }

    /**
     * 翻译
     * 
     * @param type
     * @param code
     * @return
     */
    @SuppressWarnings("unchecked")
    public String translate(String type, String code) {
        HashMap<String, Object> key = new HashMap<>();
        key.put("dataType", type);
        key.put("dataCode", code);
        Map<String, Object> po = (Map<String, Object>) ORMService.getInstance().findByPk("CmBaseData", key);
        if (po != null) {
            return (String) po.get("showName");
        } else {
            return null;
        }
    }

    /**
     * 获取翻译列表
     * 
     * @param type
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Code2NameVO> getList(String type, String condition) {
        String andCondition = "";
        if (condition != null && !"".equals(condition)) {
            andCondition = condition;
        }
        List<Map<String, Object>> list = ORMService.getInstance().queryHQL(
                "from CmBaseData a where a.dataType = ? " + andCondition + " order by sort asc", type);
        List<Code2NameVO> result = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> obj : list) {
                VO vo = new VO();
                vo.setCode(obj.get("dataCode"));
                vo.setShowName((String) obj.get("showName"));
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 获取翻译树
     * 
     * @param type
     * @param condition
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Code2NameTreeVO> getTrees(String type, String condition) {
        String andCondition = "";
        if (condition != null && !"".equals(condition)) {
            andCondition = condition;
        }
        List<Map<String, Object>> list = ORMService.getInstance().queryHQL(
                "from CmBaseData a where a.dataType = ? " + andCondition + " order by sort asc", type);
        List<Code2NameTreeVO> result = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> obj : list) {
                TreeVO vo = new TreeVO();
                vo.setCode(obj.get("dataCode"));
                vo.setShowName((String) obj.get("showName"));
                vo.setParentCode(obj.get("parentCode"));
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 获取翻译树
     * 
     * @param type
     * @return
     */
    public List<Code2NameTreeVO> getTrees(String type) {
        return getTrees(type, null);
    }

    /**
     * 获取翻译列表
     * 
     * @param type
     * @return
     */
    public List<Code2NameVO> getList(String type) {
        return getList(type, null);
    }
}

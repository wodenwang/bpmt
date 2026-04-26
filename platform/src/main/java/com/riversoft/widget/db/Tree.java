/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.widget.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.riversoft.util.jackson.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.riversoft.core.db.po.Code2NameTreeVO;
import com.riversoft.core.db.po.Code2NameVO;
import com.riversoft.core.exception.ExceptionType;
import com.riversoft.core.exception.SystemRuntimeException;
import com.riversoft.core.web.widget.DefaultWidget;
import com.riversoft.core.web.widget.FormValue;
import com.riversoft.core.web.widget.WidgetAnnotation;
import com.riversoft.platform.db.BaseDataService;
import com.riversoft.platform.db.Code2NameService;
import com.riversoft.util.ReflectionUtils;

/**
 * tree 控件处理器<br>
 * tree[DB_TYPE]:字典翻译<br>
 * 
 * @author Woden
 * 
 */
@WidgetAnnotation(cmd = "tree", ftl = "classpath:widget/{mode}/db/tree.ftl")
public class Tree extends DefaultWidget {

    private static final Logger logger = LoggerFactory.getLogger(Tree.class);

    /**
     * select下拉框值
     */
    private Map<Object, Code2NameVO> dataMap = new HashMap<>();
    private List<Code2NameTreeVO> list = new ArrayList<>();
    private boolean codeFlag = false;// 是否在下拉框展示code

    @Override
    public void prepareMap(Map<String, Object> map) {
        map.put("codeFlag", codeFlag);
        map.put("dataMap", dataMap);
        map.put("treeJson", JsonMapper.defaultMapper().toJson(list));
    }

    @Override
    public void setParams(FormValue... values) {
        if (values == null || values.length < 1 || values[0] == null || values[0].getName() == null) {
            throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "tree组件配置出错。");
        }

        if (values[0].getName().startsWith("$")) {// ORM模式
            if (values.length < 4) {// 至少4个入参
                throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "tree组件配置出错。");
            }
            String entity = values[0].getName().substring(1);
            String code = values[1].getName();
            String parentCode = values[2].getName();
            String name = values[3].getName();
            String condition = values.length > 4 ? values[4].getName() : null;
            if (values.length > 5) {
                codeFlag = !"false".equalsIgnoreCase(values[5].getName());
            }
            list.addAll(Code2NameService.getInstance().getTreesORM(entity, code, parentCode, name, condition));
        } else if (values[0].getName().startsWith("#")) {// JDBC模式
            if (values.length < 4) {// 至少4个入参
                throw new SystemRuntimeException(ExceptionType.CONFIG_WIDGET, "tree组件配置出错。");
            }
            String table = values[0].getName().substring(1);
            String code = values[1].getName();
            String parentCode = values[2].getName();
            String name = values[3].getName();
            String condition = values.length > 4 ? values[4].getName() : null;
            if (values.length > 5) {
                codeFlag = !"false".equalsIgnoreCase(values[5].getName());
            }
            list.addAll(Code2NameService.getInstance().getTreesJDBC(table, code, parentCode, name, condition));
        } else if (values[0].getName().startsWith("@")) {// 枚举翻译
            String className = values[0].getName().substring(1);
            Class<?> klass;
            try {
                klass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new SystemRuntimeException("枚举类[" + className + "]不存在。", e);
            }
            Code2NameTreeVO[] vos = (Code2NameTreeVO[]) ReflectionUtils.getMethodValue(klass, klass, "values",
                    new Class[] {}, new Object[] {});
            if (values.length > 51) {
                codeFlag = !"false".equalsIgnoreCase(values[1].getName());
            }
            list.addAll(Arrays.asList(vos));
        } else {
            String type = values[0].getName();
            String condition = values.length > 1 ? values[1].getName() : null;
            if (values.length > 2) {
                codeFlag = !"false".equalsIgnoreCase(values[2].getName());
            }
            list.addAll(BaseDataService.getInstance().getTrees(type, condition));
        }

        for (Code2NameVO vo : list) {
            dataMap.put(vo.getCode(), vo);
        }
    }

    @Override
    public String show(Object value) {
        if (dataMap.containsKey(value)) {
            if (codeFlag) {
                return "[" + value + "]" + dataMap.get(value).getShowName();
            } else {
                return dataMap.get(value).getShowName();
            }
        }

        return "";
    }

    @Override
    public Object code(String showName) {
        // 这里不需要反向翻译，只需把中括号[]的值解析出来即可
        Pattern p = Pattern.compile("\\[([^\\]]*)\\]");
        Matcher m = p.matcher(showName);
        logger.debug("解析showName:[" + showName + "]");
        while (m.find()) {
            String result = m.group(1);
            logger.debug("解析到值：[" + result + "]");
            return result;
        }

        return showName;
    }
}

/*
 * $HeadURL: $
 * $Id: $
 * Copyright (c) 2013 by Riversoft System, all rights reserved.
 */
package com.riversoft.module.${settings.module?lower_case};

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.riversoft.core.db.DataCondition;
import com.riversoft.core.db.DataPackage;
import com.riversoft.core.web.Actions;
import com.riversoft.core.web.Actions.Util;
import com.riversoft.platform.web.crud.BaseAnnotationAction;
import com.riversoft.platform.web.crud.annotation.CRUDAction;
<#if settings.hasPage>
import com.riversoft.platform.web.crud.annotation.CRUDPageFile;
import com.riversoft.platform.web.crud.annotation.CRUDPageFile.PageType;
</#if>

/**
 * 增删改查 ${settings.module}
 * 
 * @author ${settings.author}
 */
 <#compress>
@CRUDAction(hbm = "/hbm/${settings.hbmName}"<#if settings.hasPage>, pages = {
        <#if settings.hasMain>@CRUDPageFile(type = PageType.MAIN, val = "${settings.pagePath}/main.jsp")</#if><#if settings.hasList><#if settings.hasMain>,</#if>
        @CRUDPageFile(type = PageType.LIST, val = "${settings.pagePath}/list.jsp")</#if><#if settings.hasDetail><#if settings.hasMain || settings.hasList>,</#if>
        @CRUDPageFile(type = PageType.DETAIL, val = "${settings.pagePath}/detail.jsp")</#if><#if settings.hasForm><#if settings.hasMain || settings.hasList || settings.hasDetail>,</#if>
        @CRUDPageFile(type = PageType.FORM, val = "${settings.pagePath}/form.jsp")</#if><#if settings.hasBatch><#if settings.hasMain || settings.hasList || settings.hasDetail || settings.hasForm>,</#if>
        @CRUDPageFile(type = PageType.BATCH, val = "${settings.pagePath}/batch.jsp")</#if> }</#if>)
</#compress>

public class ${settings.module?cap_first}CRUDAction extends BaseAnnotationAction {

    // 代码生成器设计:
    // 需要有5个页面配置开关,分别配置是否生成main.jsp,list.jsp等页面;
    // 生成文件采用ant/mvn + freemarker来做,最好采用单独的配置文件(xml或properties均可),配置一下hbm文件和各个开关即可生成代码.
    // 无设置的则不需要生成@CRUDPageFile配置与相应jsp文件.

    // 生成文件约定:(具体可以参考BaseAnnotationAction的逻辑)
    // createDate和updateDate在新增时不展示,在更新时是readonly;
    // 对于字符串的,长度大于200则使用textarea,否则用text;
    // 默认生成主键的查询条件,如果有busiName字段的则生成busiName字段的模糊查询条件(_sl_busiName);

    // 未来的扩展:
    // 需要有若干action方法开关,会生成覆盖BaseCRUDAction相应方法的方法;

    // 通用CRUD框架的配置：
    // @CRUDAction.value 必填，指定当前Action关联的hbm文件路径。
    // @CRUDAction 其他属性都属于“白名单”性质，即不填的话模块就以hbm里面配置的信息为准；填的话就按照实际填写为准。

    <#if settings.listOverwrite>
    /**
     * 根据开关生成覆盖的方法,如此方法.
     */
    @Override
    public void list(HttpServletRequest request, HttpServletResponse response) {
        // 获取分页信息
        int start = Util.getStart(request);
        int limit = Util.getLimit(request);

        // 获取排序信息
        String field = Util.getSortField(request);
        String dir = Util.getSortDir(request);

        // 查询条件
        DataCondition condition = new DataCondition();
        condition.setOrderBy(field, dir);
        Map<String, String[]> parameterMap = request.getParameterMap();
        Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String[]> entry = it.next();
            String key = entry.getKey();
            String value = parameterMap.get(key)[0];
            if (key.matches("[_][a-z]+[_][a-zA-Z0-9]+") && !StringUtils.isEmpty(value)) {// 判断格式
                condition.addCondition(key, value);
            }
        }

        DataPackage dp = service.queryPackage("Demo", start, limit, condition.toEntity());
        // 设置到页面
        request.setAttribute("dp", dp);

        // 扩展模式
        List<?> rules = service.query("DemoRule", new DataCondition().setOrderByAsc("sort").toEntity());
        request.setAttribute("rules", rules);

        Actions.includePage(request, response, Util.getPagePath(request, "list.jsp"));
    }
    </#if>
}

<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<%--数据表格 --%>
<#noparse>
<table class="ws-table" form="${_zone}_form">
</#noparse>
  <thead>
    <tr>
      <th check="true"></th>
      <th style="width: 7.8em;">操作</th>
    <#list ids as id>
      <th field="${id.name}">${id.comment}</th>
    </#list>
    <#list columns as column>
      <th field="${column.name}">${column.comment}</th>
    </#list>
      <%--扩展模式,由生成文件时决定是否需要生成 --%>
    <#if vars??>
      <#list vars as var>
      <th field="vars['${var.fieldKey}'].${var.name}">${var.comment}</th>
        </#list>
    </#if>
    </tr>
  </thead>
  
  <#noparse>
  <tbody>
    <c:forEach items="${dp.list}" var="vo">
      <tr>
        <td check="true" value="{id:'${vo.id}'}"></td>
        <td class="center ws-group">
          <button icon="document" text="false" type="button" onclick="show(this);" value="{id:'${vo.id}'}">查看</button>
          <button icon="pencil" text="false" type="button" onclick="edit(this);" value="{id:'${vo.id}'}">编辑</button>
          <button icon="trash" text="false" type="button"  onclick="delete(this);" value="{id:'${vo.id}'}">删除</button>
        </td>
  </#noparse>      
  <#list ids as id>
        <#noparse><td class="center">${wcm:widget('text',</#noparse> ${id.name})}</td>
  </#list>
  <#list columns as column>
        <#noparse><td class="center">${wcm:widget('text',</#noparse> ${column.name})}</td>
  </#list>
        <%--扩展模式,由生成文件时决定是否需要生成 --%>
      </tr>
    </c:forEach>
  </tbody>
  
  <tr>
    <th class="ws-bar">
      <div class="left" style="float: left;">
        <span class="ws-group">
          <button type="button" icon="trash" text="true" onclick="deleteAll();">删除</button>
        </span> 
        <span class="ws-group">  
          <button type="button" icon="arrowthickstop-1-s" text="true"  onclick="downloadBath();">导出当前</button>
          <button type="button" icon="arrowthickstop-1-s" text="true"  onclick="downloadBath('all');">导出所有</button>
        </span>
      </div>
      <div class="ws-group right" style="float: right;">
        <button type="button" icon="plusthick" text="true" onclick="batch();">批处理</button>
        <button type="button" icon="plus" text="true" onclick="create();">新增</button>
      </div>
    </th>
  </tr>
</table>  

<#noparse>
<%-- 分页  --%>
<wcm:page dp="${dp}" form="${_zone}_form" />
</#noparse>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
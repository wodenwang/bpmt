<#noparse>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:if test="${config!=null && config.formJs != null }">
  <script type="text/javascript">
    $(function() {
      $
      {
        config.formJs
      }
    });
  </script>
</c:if>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post"
  id="${_zone}_form"
  option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">
  <c:if test="${!isCreate}">
    <input type="hidden" name="_key" value="${wcm:json(vo)}">
  </c:if>

  <table class="ws-table">
    <c:if test="${config!=null&&config.fields!=null}">
      <c:forEach items="${config.fields}" var="field">
        <c:if
          test="${field.inForm && ((isCreate && field.createState !=  'none') || (!isCreate && field.editState != 'none'))}">
          <tr>
            <th>${field.busiName}</th>
            <td><wcm:widget name="${field.name}" cmd="${field.widgetCmd}"
                value="${vo!=null?vo[field.name]:'' }"
                state="${isCreate ?  field.createState : field.editState}">不支持命令</wcm:widget></td>
          </tr>
        </c:if>
      </c:forEach>
    </c:if>
    <c:if test="${rules!=null && fn:length(rules)>0}">
      <c:forEach items="${rules}" var="rule">
        <tr>
          <th>${rule.busiName}</th>
          <td><wcm:widget name="vars.${rule.fieldKey}"
              cmd="${rule.widgetCmd}"
              value="${vo!=null?wcm:property(vo.vars[rule.fieldKey],rule.itemName):'' }">不支持命令</wcm:widget>
          </td>
        </tr>
      </c:forEach>
    </c:if>
    <tr>
      <th class="ws-bar ">
        <div class="ws-group">
          <button type="button" icon="closethick" text="true"
            onclick="closeTab('${_zone}')">关闭</button>
          <button type="reset" icon="arrowreturnthick-1-w" text="true">重置</button>
          <button type="button" icon="check" text="true"
            onclick="submitForm('${_zone}_form','${_zone}');">提交</button>
        </div>
      </th>
    </tr>
  </table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
</#noparse>
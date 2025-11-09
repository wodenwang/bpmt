<#noparse>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:if test="${config!=null && config.detailJs != null }">
  <script type="text/javascript">
    $(function() {
      ${config.detailJs}
    });
  </script>
</c:if>

<table class="ws-table">
  <c:if test="${config!=null&&config.fields!=null}">
    <c:forEach items="${config.fields}" var="field">
      <c:if test="${field.inDetail}">
        <tr>
          <th>${field.busiName}</th>
          <td>${wcm:widget(field.widgetCmd,vo[field.name])}</td>
        </tr>
      </c:if>
    </c:forEach>
  </c:if>

  <c:if test="${rules!=null && fn:length(rules)>0}">
    <c:forEach items="${rules}" var="rule">
      <c:if test="${field.inDetail}">
        <tr>
          <th>${rule.busiName}</th>
          <td>${wcm:widget(rule.widgetCmd,wcm:property(vo.vars[rule.fieldKey],rule.itemName))}</td>
        </tr>
      </c:if>
    </c:forEach>
  </c:if>

  <tr>
    <th class="ws-bar ">
      <div class="ws-group">
        <button type="button" icon="closethick" text="true"
          onclick="closeTab('${_zone}')">关闭</button>
      </div>
    </th>
  </tr>
</table>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
</#noparse>
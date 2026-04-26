<#noparse>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:if test="${config!=null && config.batchJs != null }">
  <script type="text/javascript">
    $(function() {
      ${config.batchJs}
    });
  </script>
</c:if>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submitBatch.shtml" method="post"
  id="${_zone}_form"
  option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">

  <table class="ws-table">
    <tr>
      <th>批处理类型</th>
      <td class="ws-group"><input type="radio" id="radio1" name="type"
        value="1" checked="checked" /><label for="radio1">新增</label> <input
        type="radio" id="radio2" name="type" value="2" /><label
        for="radio2">修改</label></td>
    </tr>
    <tr>
      <th>数据文件</th>
      <td><input type="file" name="file"
        class="{required:true,extension:'xls|xlsx'}" /></td>
    </tr>
    <tr>
      <th class="ws-bar ">
        <div class="ws-group">
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
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<c:if test="${wpf:check(config.table.weixin.pri)}">
	<%
		//main页面直接转发到list页
			request.getRequestDispatcher(request.getAttribute("_action") + "/list.shtml").forward(request, response);
	%>
</c:if>

<%@ include file="/include/h5_head.jsp"%>

<div class="am-container am-margin-top">
	<div class="am-alert am-alert-danger">
		<h4>
			<i class="am-icon-times-circle"></i> 系统提示
		</h4>
		<div class="am-margin-top">无访问权限</div>
	</div>

	<button type="button" class="am-btn am-btn-default am-radius am-btn-block" onclick="wx.closeWindow();">关闭</button>
</div>

<%@ include file="/include/h5_bottom.jsp"%>
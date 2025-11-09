<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div tabs="true">
	<div title="人员分配" init="${_acp}/setGroupRoleUser.shtml?groupKey=${param.groupKey}&roleKey=${param.roleKey}"></div>
	<div title="权限设置" init="${_acp}/editGroupRolePri.shtml?groupKey=${param.groupKey}&roleKey=${param.roleKey}"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
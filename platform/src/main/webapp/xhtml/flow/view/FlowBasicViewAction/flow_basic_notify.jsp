<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<input type="hidden" name="hasNotify" value="true" />
<table class="ws-table">
	<tr>
		<th>通知方式</th>
		<td><wcm:widget name="notify.msgType" cmd="checkbox[@com.riversoft.platform.translate.NotifyMsgType]" value="${vo.msgType}" /></td>
	</tr>
</table>
<div tabs="true" button="left">
	<div title="邮件通知模板">
		<table class="ws-table">
			<tr>
				<th>标题(脚本类型)</th>
				<td><wcm:widget name="notify.mailSubjectType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.mailSubjectType}" /></td>
			</tr>
			<tr>
				<th>标题(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程实体;task:当前任务实体;">(提示)</font></th>
				<td><wcm:widget name="notify.mailSubjectScript" cmd="codemirror[groovy]" value="${vo.mailSubjectScript}" /></td>
			</tr>
			<tr>
				<th>内容(脚本类型)</th>
				<td><wcm:widget name="notify.mailContentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.mailContentType}" /></td>
			</tr>
			<tr>
				<th>内容(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程实体;task:当前任务实体;">(提示)</font></th>
				<td><wcm:widget name="notify.mailContentScript" cmd="codemirror[groovy]" value="${vo.mailContentScript}" /></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table">
	<tr>
		<th>${wpf:lan("#:zh[序号]:en[Serial number]#")}</th>
		<th>${wpf:lan("#:zh[节点]:en[Node]#")}</th>
		<th>${wpf:lan("#:zh[节点类型]:en[Node type]#")}</th>
		<th>${wpf:lan("#:zh[目标处理人]:en[Target Handler]#")}</th>
		<th>${wpf:lan("#:zh[节点开始时间]:en[Node start time]#")}</th>
		<th>${wpf:lan("#:zh[执行时长]:en[Execution time]#")}</th>
		<th>${wpf:lan("#:zh[处理动作]:en[Processing action]#")}</th>
		<th>${wpf:lan("#:zh[实际处理人]:en[Actual Handler]#")}</th>
		<th>${wpf:lan("#:zh[处理人部门]:en[Handler department]#")}</th>
	</tr>
	<c:forEach items="${list}" var="vo" varStatus="status">
		<tr>
			<td class="center">${status.index+1}</td>
			<td class="center">${vo.ACTIVITY_NAME}</td>
			<td class="center">${wcm:widget('select[@com.riversoft.flow.key.NodeType]',vo.NODE_TYPE)}</td>
			<td class="center">${vo.ASSIGNEE}</td>
			<td class="center">${wcm:widget('date[datetime]',vo.TASK_BEGIN_DATE)}</td>
			<td class="center"><c:choose>
					<c:when test="${vo.TASK_END_DATE==null}">
						<span style="color: blue; font-weight: bold;">${wpf:lan("#:zh[进行中]:en[Ongoing]#")}</span>
					</c:when>
					<c:otherwise>
						<span>${wpf:formatDuring(wpf:compareDate(vo.TASK_END_DATE,vo.TASK_BEGIN_DATE,'s'))}</span>
					</c:otherwise>
				</c:choose></td>
			<td class="center">${vo.SEQUENCE_FLOW_NAME}</td>
			<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.TASK_UID)}</td>
			<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsGroup;groupKey;busiName]',vo.TASK_GROUP)}</td>
		</tr>
	</c:forEach>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
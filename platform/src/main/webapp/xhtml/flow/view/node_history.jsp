<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div accordion="true" multi="true">
	<c:forEach items="${list}" var="vo" varStatus="status">
		<div title="${vo.ACTIVITY_NAME}[${vo.TASK_END_DATE==null?'进行中':'已处理'}]">

			<table class="ws-table">
				<tr>
					<th>${wpf:lan("#:zh[节点]:en[Node]#")}</th>
					<td style="width: 400px;">${vo.ACTIVITY_NAME}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[节点类型]:en[Node Type]#")}</th>
					<td>${wcm:widget('select[@com.riversoft.flow.key.NodeType]',vo.NODE_TYPE)}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[目标处理人]:en[Handler]#")}</th>
					<td>${vo.ASSIGNEE}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[节点开始时间]:en[Node Start time]#")}</th>
					<td>${wcm:widget('date[datetime]',vo.TASK_BEGIN_DATE)}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[执行时长]:en[Execution time]#")}</th>
					<td><c:choose>
							<c:when test="${vo.TASK_END_DATE==null}">
								<span style="color: blue; font-weight: bold;">${wpf:lan("#:zh[进行中]:en[Ongoing]#")}</span>
							</c:when>
							<c:otherwise>
								<span>${wpf:formatDuring(wpf:compareDate(vo.TASK_END_DATE,vo.TASK_BEGIN_DATE,'s'))}</span>
							</c:otherwise>
						</c:choose></td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[处理动作]:en[Processing action]#")}</th>
					<td>${vo.SEQUENCE_FLOW_NAME}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[实际处理人]:en[Actual Handler]#")}</th>
					<td>${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.TASK_UID)}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[实际处理人部门]:en[Handler Department]#")}</th>
					<td>${wcm:widget('select[$com.riversoft.platform.po.UsGroup;groupKey;busiName]',vo.TASK_GROUP)}</td>
				</tr>
				<tr>
					<th>${wpf:lan("#:zh[执行情况备注]:en[Note]#")}</th>
					<td>${vo.EXECUTION_MEMO}</td>
				</tr>
			</table>
		</div>
	</c:forEach>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
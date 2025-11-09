<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<table class="ws-table" form="${_form}">
	<tr>
		<th style="width: 50px;">ID</th>
		<th style="width: 150px;">${wpf:lan("#:zh[操作时间]:en[Operation time]#")}</th>
		<th style="width: 70px;">${wpf:lan("#:zh[操作人]:en[Operator]#")}</th>
		<th style="width: 70px;">${wpf:lan("#:zh[操作类型]:en[Operation type]#")}</th>
		<th style="width: 70px;">${wpf:lan("#:zh[字段]:en[Field]#")}</th>
		<th style="min-width: 200px;">${wpf:lan("#:zh[旧值]:en[Old value]#")}</th>
		<th style="min-width: 200px;">${wpf:lan("#:zh[新值]:en[New value]#")}</th>
	</tr>
	<c:forEach items="${dp.list}" var="vo" varStatus="status">
		<tr>
			<td class="center">${vo.LOG_ID}</td>
			<c:choose>
				<c:when test="${status.index == 0 || !(vo.BATCH_ID eq dp.list[status.index - 1].BATCH_ID)}">
					<td class="center">${wcm:widget('date[datetime]',vo.OPR_TIME)}</td>
					<td class="center">${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',vo.OPR_UID)}</td>
					<td class="center">${wcm:widget('select[@com.riversoft.module.dyn.DynamicTableCRUDService$OprType]',vo.OPR_TYPE)}</td>
				</c:when>
				<c:otherwise>
					<td class="center"></td>
					<td class="center"></td>
					<td class="center"></td>
				</c:otherwise>
			</c:choose>

			<td class="center">${wpf:lan(vo.FIELD_DISPLAY)}</td>

			<c:choose>
				<c:when test="${fn:length(vo.OLD_VAL)>256||fn:length(vo.NEW_VAL)>256}">
					<td class="left" style="word-break: break-all;">${fn:length(vo.OLD_VAL)<=256?vo.OLD_VAL:'[查看内容]'}</td>
					<td class="left" style="word-break: break-all;">${fn:length(vo.NEW_VAL)<=256?vo.NEW_VAL:'[查看内容]'}</td>
				</c:when>
				<c:when test="${fn:length(vo.OLD_DISPLAY)>256||fn:length(vo.NEW_DISPLAY)>256}">
					<td class="left" style="word-break: break-all;">${vo.OLD_VAL}</td>
					<td class="left" style="word-break: break-all;">${vo.NEW_VAL}</td>
				</c:when>
				<c:otherwise>
					<td class="left" style="word-break: break-all;">${vo.OLD_DISPLAY}</td>
					<td class="left" style="word-break: break-all;">${vo.NEW_DISPLAY}</td>
				</c:otherwise>
			</c:choose>
		</tr>
	</c:forEach>
</table>

<wcm:page dp="${dp}" form="${_form}"></wcm:page>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
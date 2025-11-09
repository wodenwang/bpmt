<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<table class="ws-table">

	<tr>
		<th>表名</th>
		<td>${table.name}</td>
	</tr>
	<tr>
		<th>表展示名</th>
		<td>${table.description}</td>
	</tr>
	<tr>
		<th>开启缓存</th>
		<td>${wcm:widget('select[YES_NO]',table.cacheFlag)}</td>
	</tr>
	<tr>
		<th>建表人</th>
		<td>${wcm:widget('select[$com.riversoft.platform.po.UsUser;uid;busiName]',table.createUid)}</td>
	</tr>
	<tr>
		<th>创建时间</th>
		<td>${wcm:widget('date',table.createDate)}</td>
	</tr>
	<tr>
		<th>更新时间</th>
		<td>${wcm:widget('date',table.updateDate)}</td>
	</tr>
	<tr>
		<th>表展示名</th>
		<td>
			<table class="ws-table">
				<tr>
					<th>主键类型</th>
					<th>字段名</th>
					<th>展示名</th>
					<th>类型</th>
					<th>是否必须</th>
					<th>长度(总长度,小数精度)</th>
					<th>默认值</th>
				</tr>
				<c:forEach items="${table.tbColumns}" var="column">
					<tr tip="${column.memo!=null&&column.memo!=''}"
						title="${column.memo}">
						<td class="center">${column.primaryKey?'主键':'非主键'}${column.autoIncrement?',自动递增':''}</td>
						<td class="center">${column.name}</td>
						<td class="center">${column.description}</td>
						<td class="center">${wcm:widget('select[@com.riversoft.platform.db.Types]',column.mappedTypeCode)}</td>
						<td class="center">${column.required?'是':'否'}</td>
						<td class="center">${column.totalSize},${column.scale}</td>
						<td class="center">${column.defaultValue}</td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<tr>
		<th class="ws-bar ">
			<div class="ws-group">
				<button type="button" icon="closethick" text="true"
					onclick="Ui.closeTab('${_zone}');">关闭</button>
			</div>
		</th>
	</tr>
</table>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
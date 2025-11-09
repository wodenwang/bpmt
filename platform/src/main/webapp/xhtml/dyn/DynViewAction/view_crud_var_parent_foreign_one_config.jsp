<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.parentColumn']", $zone).change(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var description = $('option[value=' + val + ']', $this).html();
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(description);
			}
		});

		$("[name$='.parentColumn']", $zone).change();
	});
</script>
<input type="hidden" name="${param.type}" value="${param.pixel}" />
<table class="ws-table">
	<tr>
		<th>关联表字段</th>
		<td><select name="${param.pixel}.parentColumn"
			class="chosen needValid {required:true}">
				<c:forEach items="${parentColumns}" var="column">
					<option value="${column.name}">[${column.name}]${column.description}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr>
		<th>本表字段</th>
		<td><select name="${param.pixel}.mainColumn"
			class="chosen needValid {required:true}">
				<c:forEach items="${columns}" var="column">
					<option value="${column.name}">[${column.name}]${column.description}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr>
		<th>描述</th>
		<td><wcm:widget name="${param.pixel}.description" cmd="textarea"></wcm:widget></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		$("#${_zone}_list_form").submit();

		Core.fn('${_zone}_list_zone', 'select', function(json) {
			Core.fn($zone, 'select')(json);
		});
	});
</script>

<form action="${_acp}/list.shtml" query="true" zone="${_zone}_list_zone" id="${_zone}_list_form">
	<input type="hidden" name="type" value="${param.type}" /><input type="hidden" name="menuType" value="${param.menuType}" /><input type="hidden" name="mpFlag" value="${param.mpFlag}" />
	<c:if test="${param.type == 'self'}">
		<table class="ws-table">
			<tr>
				<th>逻辑主键(模糊)</th>
				<td><input type="text" name="_sl_commandKey" /></td>
				<th>展示名</th>
				<td><input type="text" name="_sl_busiName" /></td>
			</tr>
			<tr>
				<th>描述</th>
				<td><input type="text" name="_sl_description" /></td>
				<th>脚本</th>
				<td><wcm:widget name="_sl_logicScript" cmd="text" /></td>
			</tr>
			<tr>
				<th class="ws-bar ">
					<div class="ws-group right">
						<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
						<button type="submit" icon="search" text="true">查询</button>
					</div>
				</th>
			</tr>
		</table>
	</c:if>
</form>

<div id="${_zone}_list_zone"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
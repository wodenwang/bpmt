<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('#${_zone}_list_form').submit();
	});
</script>
<form zone="${_zone}_list" action="${_acp}/selectUserList.shtml"
	query="true" id="${_zone}_list_form" method="get">
	<table class="ws-table">
		<tr>
			<th>用户名</th>
			<td><wcm:widget cmd="text" name="_sl_uid" /></td>
			<th>用户展示名</th>
			<td><wcm:widget cmd="text" name="_sl_busiName" /></td>
		</tr>
		<tr>
			<th class="ws-bar">
				<div class="right ws-group">
					<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
					<button icon="search" type="submit">查询</button>
				</div>
			</th>
		</tr>
	</table>
</form>

<div id="${_zone}_list"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
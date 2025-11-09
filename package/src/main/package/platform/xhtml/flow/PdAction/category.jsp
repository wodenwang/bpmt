<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('form', $zone).submit(function(event) {
			event.preventDefault();
			var $this = $(this);

			Ajax.form('${_zone}_msg_zone', $this, {
				confirmMsg : '确认保存?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});
		});
	});
</script>

<div id="${_zone}_msg_zone"></div>

<form action="${_acp}/submitCategory.shtml" sync="true">
	<input type="hidden" name="pdKey" value="${pd.key}" />
	<table class="ws-table">
		<tr>
			<th>流程KEY</th>
			<td>${pd.key}</td>
		</tr>
		<tr>
			<th>所属类别</th>
			<td><wcm:widget name="category" cmd="text" value="${pd.category}" /></td>
		</tr>
		<tr>
			<th>历史版本</th>
			<td>
				<ul style="margin: 0px; padding: 0px;">
					<c:forEach items="${pds}" var="v" varStatus="status">
						<li style="list-style: none; float: left; margin-right: 10px; margin-left: 0px;"><a style="${status.index==0?'color:red;':''}" href="javascript:void(0);"
							onclick="Core.fn('${_zone}','config')('${v.id}');">版本:${v.version} </a></li>
					</c:forEach>
				</ul>
			</td>
		</tr>
		<tr>
			<th class="ws-bar">
				<div class="ws-group">
					<button icon="disk" type="submit">保存</button>
				</div>
			</th>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
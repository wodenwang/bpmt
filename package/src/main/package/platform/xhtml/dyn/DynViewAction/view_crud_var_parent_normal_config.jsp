<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.var']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});
		$("select[name$='.tableName']", $zone).change(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var busiName = $("option[value='" + val + "']", $this).attr("busiName");
				$("[name$='.busiName']", $zone).val(busiName);
				$("[name$='.busiName']", $zone).blur();

				Ajax.post('${_zone}_parent_foreign_zone', '${_acp}/parentViewForeignConfig.shtml', {
					data : {
						parentTableName : val,
						tableName : '${param.tableName}',
						type : '${param.pixel}.foreigns',
						pixel : '${param.pixel}.B' + Core.nextSeq()
					}
				});
			}
		});
	});
</script>

<input type="hidden" name="${param.type}" value="${param.pixel}" />
<table class="ws-table">
	<tr>
		<th>关联表</th>
		<td><wcm:widget name="${param.pixel}.tableName"
				cmd="select[$com.riversoft.platform.po.TbTable(请选择);name;description;null;true]{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>变量名</th>
		<td><wcm:widget cmd="text{required:true}"
				name="${param.pixel}.var" /></td>
	</tr>
	<tr>
		<th>描述</th>
		<td><wcm:widget name="${param.pixel}.description" cmd="textarea" /></td>
	</tr>
</table>

<div id="${_zone}_parent_foreign_zone"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
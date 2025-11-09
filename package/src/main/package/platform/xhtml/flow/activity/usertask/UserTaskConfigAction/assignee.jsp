<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.description']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var $tab = $this.parents('div.ui-tabs-panel:first');
				var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
				var html = $a.html();
				var batch = html.substring(0, html.indexOf('-'));
				$a.html(batch + "-" + val);
			}
		});

		$("[name$='.batchNum']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var $tab = $this.parents('div.ui-tabs-panel:first');
				var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
				var html = $a.html();
				var description = html.substring(html.indexOf('-') + 1);
				$a.html(val + "-" + description);
			}
		});

		$("[name$='.batchNum']", $zone).blur();

		//根据分配规则出现字段
		$("[name$='.allocateType']", $zone).change(function() {
			var val = $(this).val();
			if (val == 0) {
				$('tr[name=group]', $zone).hide();
				$('tr[name=role]', $zone).hide();
				$('tr[name=uid]', $zone).show();
			} else {
				$('tr[name=uid]', $zone).hide();
				if (val == 1) {
					$('tr[name=group]', $zone).show();
					$('tr[name=role]', $zone).hide();
				} else if (val == 2) {
					$('tr[name=group]', $zone).hide();
					$('tr[name=role]', $zone).show();
				} else {
					$('tr[name=group]', $zone).show();
					$('tr[name=role]', $zone).show();
				}
			}
		});

		$("[name$='.allocateType']", $zone).change();
	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="assignees" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>规则分组标号</th>
		<td><wcm:widget name="${pixel}.batchNum"
				cmd="text{required:true,digits:true}" value="1"></wcm:widget></td>
	</tr>
	<tr>
		<th>处理逻辑(脚本类型)</th>
		<td><wcm:widget name="${pixel}.decideType"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
				value="1"></wcm:widget></td>
	</tr>
	<tr>
		<th>处理逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
		<td><wcm:widget name="${pixel}.decideScript"
				cmd="codemirror[groovy]{required:true}" value="return true;"></wcm:widget></td>
	</tr>
	<tr>
		<th>分配类型</th>
		<td><wcm:widget name="${pixel}.allocateType"
				cmd="select[@com.riversoft.module.flow.activity.usertask.AllocateType]{required:true}"
				value="0" /></td>
	</tr>
	<tr name="uid">
		<th>目标用户(脚本类型)</th>
		<td><wcm:widget name="${pixel}.uidType"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
				value="1"></wcm:widget></td>
	</tr>
	<tr name="uid">
		<th>目标用户(脚本)<br /> <font color="red" tip="true"
			title="vo:订单实体;返回待分配的用户ID.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.uidScript"
				cmd="codemirror[groovy]{required:true}" value="return 'admin';"></wcm:widget></td>
	</tr>
	<tr name="uid">
		<th>是否独占</th>
		<td><wcm:widget name="${pixel}.uniqueFlag"
				cmd="radio[YES_NO]{required:true}" value="0"></wcm:widget></td>
	</tr>
	<tr name="group">
		<th>目标组织(脚本类型)</th>
		<td><wcm:widget name="${pixel}.groupType"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
				value="1"></wcm:widget></td>
	</tr>
	<tr name="group">
		<th>目标组织(脚本)<br /> <font color="red" tip="true"
			title="vo:订单实体;返回待分配的组织Key.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.groupScript"
				cmd="codemirror[groovy]{required:true}" value="return 'admin';"></wcm:widget></td>
	</tr>
	<tr name="role">
		<th>目标角色(脚本类型)</th>
		<td><wcm:widget name="${pixel}.roleType"
				cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
				value="1"></wcm:widget></td>
	</tr>
	<tr name="role">
		<th>目标角色(脚本)<br /> <font color="red" tip="true"
			title="vo:订单实体;返回待分配的角色Key.">(提示)</font></th>
		<td><wcm:widget name="${pixel}.roleScript"
				cmd="codemirror[groovy]{required:true}" value="return 'admin';"></wcm:widget></td>
	</tr>
	<tr>
		<th>描述</th>
		<td><wcm:widget name="${pixel}.description"
				cmd="textarea{required:true}" /></td>
	</tr>
</table>

<%@ include file="/include/html_bottom.jsp"%>
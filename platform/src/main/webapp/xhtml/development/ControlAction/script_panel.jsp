<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=addVar]', $zone).click(function() {
			var $tabs = $('#${_zone}_tabs');
			Ajax.tab($tabs, '${_acp}/scriptVariableTab.shtml', {
				data : {
					pixel : 'script.' + Core.nextSeq()
				}
			});
		});

		//真实执行
		$('button[name=exec]', $zone).click(function() {
			var $form = $("#${_zone}_copy_form", $zone);
			$form.children().remove();
			$.each($('textarea,select,input', $("#${_zone}_form")), function() {
				var $this = $(this);
				var $copy = $('<textarea name="' + $this.attr('name') + '"></textarea>');
				$copy.val($this.val());
				$form.append($copy);
			});

			Ui.confirmPassword("此操作会改变当前系统的真实数据,请否继续?", function() {
				Ajax.form('${_zone}_msg', $form, {
					loading : true
				});
			});
		});
	});
</script>

<div style="display: none;">
	<form action="${_acp}/submitScriptReal.shtml" sync="true" id="${_zone}_copy_form"></form>
</div>

<form action="${_acp}/submitScript.shtml" zone="${_zone}_msg" id="${_zone}_form">
	<div tabs="true" button="left" id="${_zone}_tabs"></div>
	<div class="ws-bar">
		<div class="ws-group left">
			<button icon="plus" type="button" name="addVar">增加变量</button>
			<button icon="wrench" type="submit" tip="true" title="在一个模拟当前系统环境,但完全隔离的沙箱内运行,不会对现有系统的数据造成影响.">模拟执行</button>
		</div>
		<div class="right">
			<button type="button" icon="wrench" name="exec">真实执行</button>
		</div>
	</div>
	<table class="ws-table">
		<tr>
			<th>选择语言</th>
			<td><wcm:widget name="type" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
		</tr>
		<tr>
			<th>脚本</th>
			<td><wcm:widget name="script" cmd="codemirror[groovy;true]"></wcm:widget></td>
		</tr>
	</table>
</form>

<div id="${_zone}_msg">
	<div class="ws-msg info">请执行脚本.</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
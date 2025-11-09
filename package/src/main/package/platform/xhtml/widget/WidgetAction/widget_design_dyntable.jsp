<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $cmd = $('button[name=ok]', $zone);

		$cmd.click(function() {
			var val = $(this).val();
			$('select[name=cmdType]', $zone).change();
			Core.fn($zone, 'callback')(val);
		});

		var $code = $('select[name=code]', $zone);
		var $parentCode = $('select[name=parentCode]', $zone);
		var $showValue = $('select[name=showValue]', $zone);
		var $table = $('select[name=table]', $zone);

		$('select[name=cmdType]', $zone).change(function() {
			var val = $(this).val();
			if (val == 'tree' || val == 'multitree') {
				$parentCode.parents('tr').show();
				$cmd.val(val + '[#' + $table.val() + ';' + $code.val() + ';' + $parentCode.val() + ';' + $showValue.val() + ']');
			} else {
				$parentCode.parents('tr').hide();
				if (val == 'select') {
					$cmd.val(val + '[#' + $table.val() + '(请选择);' + $code.val() + ';' + $showValue.val() + ']');
				} else {
					$cmd.val(val + '[#' + $table.val() + ';' + $code.val() + ';' + $showValue.val() + ']');
				}
			}
		});

		$code.change(function() {
			$('select[name=cmdType]', $zone).change();
		});
		$parentCode.change(function() {
			$('select[name=cmdType]', $zone).change();
		});
		$showValue.change(function() {
			$('select[name=cmdType]', $zone).change();
		});

		$table.change(function() {
			var val = $table.val();
			Ajax.json('${_acp}/dynTableJson.shtml', function(result) {
				$code.children().remove();
				$parentCode.children().remove();
				$showValue.children().remove();
				$.each(result.columns, function(index, value) {
					var $option = $('<option></option>');
					$option.val(value.name);
					$option.html('[' + value.name + ']' + value.description);
					$code.append($option.clone());
					$parentCode.append($option.clone());
					$showValue.append($option.clone());
				});
				$code.trigger("liszt:updated");
				$parentCode.trigger("liszt:updated");
				$showValue.trigger("liszt:updated");

				$cmd.prop("disabled", false).button("refresh");
				$(':radio[name=cmdType]:checked', $zone).change();

				$('select[name=cmdType]', $zone).change();
			}, {
				data : {
					table : val
				}
			});
		});

		$table.change();

	});
</script>

<table class="ws-table">
	<tr>
		<th>控件类型</th>
		<td><select name="cmdType" class="chosen">
				<option value="select">单选下拉</option>
				<option value="radio">单选卡</option>
				<option value="multiselect">多选下拉</option>
				<option value="checkbox">多选卡</option>
				<option value="tree">树形</option>
		</select></td>
	</tr>
	<tr>
		<th>动态表</th>
		<td><wcm:widget name="table" cmd="select[$com.riversoft.platform.po.TbTable;name;description;null;true]"></wcm:widget></td>
	</tr>
	<tr>
		<th>值</th>
		<td><select class="chosen" name="code"></select></td>
	</tr>
	<tr>
		<th>父ID(值)</th>
		<td><select class="chosen" name="parentCode"></select></td>
	</tr>
	<tr>
		<th>展示文字</th>
		<td><select class="chosen" name="showValue"></select></td>
	</tr>
	<tr>
		<th class="ws-bar"><button icon="check" name="ok">确定</button></th>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
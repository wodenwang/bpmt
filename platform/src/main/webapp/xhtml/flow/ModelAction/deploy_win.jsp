<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $A1 = $('input:checkbox[name=A1]', $zone);
		var $A2 = $('input:checkbox[name=A2]', $zone);
		var $A3 = $('input:checkbox[name=A3]', $zone);
		var $B1 = $('input:checkbox[name=B1]', $zone);

		var $A1_text = $('input[name=A1_text]', $zone);
		var $A2_text = $('input[name=A2_text]', $zone);
		var $A3_text = $('input[name=A3_text]', $zone);

		var $A1_zone1 = $('#${_zone}_a1_zone1');
		var $A1_zone2 = $('#${_zone}_a1_zone2');
		var $B1_zone1 = $('#${_zone}_b1_zone1');
		var $B1_zone2 = $('#${_zone}_b1_zone2');

		var $tableName = $('select[name=tableName]', $zone);
		var $basicView = $('select[name=basicView]', $zone);

		//订单表自动创建
		$A1.on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$A1_zone1.show();
				$A1_zone2.hide();

				$A1_text.prop('disabled', false);
				$B1.iCheck('check');
				$B1.iCheck('disable');
				$tableName.val('').prop('disabled', true).trigger("liszt:updated");
				$basicView.val('').prop('disabled', true).trigger("liszt:updated");
			} else {//没有选中
				$A1_zone1.hide();
				$A1_zone2.show();

				$A1_text.prop('disabled', true);
				$B1.iCheck('check');
				$B1.iCheck('enable');
				$tableName.val('').prop('disabled', false).trigger("liszt:updated");
				$basicView.val('').prop('disabled', true).trigger("liszt:updated");
			}
		});

		//基础视图自动创建
		$B1.on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$B1_zone1.show();
				$B1_zone2.hide();
				$basicView.val('').prop('disabled', true).trigger("liszt:updated");
			} else {//没有选中
				$B1_zone1.hide();
				$B1_zone2.show();
				$basicView.val('').prop('disabled', false).trigger("liszt:updated");

				//AJAX联动查询
				$tableName.change();
			}
		});

		//历史表自动创建
		$A2.on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$A2_text.prop('disabled', false);
			} else {//没有选中
				$A2_text.prop('disabled', true);
			}
		});

		//审批意见表自动创建
		$A3.on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$A3_text.prop('disabled', false);
			} else {//没有选中
				$A3_text.prop('disabled', true);
			}
		});

		//订单表联动
		$tableName.change(function() {
			var tableName = $(this).val();
			$('option[value!=""]', $basicView).remove();
			if (!$basicView.prop('disabled') && tableName != '') {
				Ajax.json('${_acp}/selectBasicView.shtml', function(list) {
					if (list != null && $.isArray(list)) {
						$.each(list, function(i, o) {
							var $option = $('<option></option>');
							$option.html(o.busiName);
							$option.val(o.viewKey);
							$basicView.append($option);
						});
						$basicView.trigger("liszt:updated");
					}
				}, {
					data : {
						tableName : tableName,
						pdKey : '${key}'
					}
				});
			}
		});

	});
</script>

<div name="errorZone" id="${_zone}_msg_zone"></div>

<form action="${_acp}/submitDeploy.shtml" sync="true" onsubmit="return false;">
	<input type="hidden" name="id" value="${param.id}" /> <input type="hidden" name="pdKey" value="${key}" />
	<table class="ws-table">
		<tr>
			<th>流程唯一KEY</th>
			<td>${key}</td>
		</tr>
		<tr>
			<th>流程名称</th>
			<td>${name}</td>
		</tr>

		<tr>
			<th rowspan="2">[A1]订单表</th>
			<td><input type="checkbox" value="1" name="A1" checked="checked" /><label>自动创建</label></td>
		</tr>
		<tr>
			<td><div id="${_zone}_a1_zone1">
					<span style="margin-right: 5px; font-weight: bold;">创建表名:</span><input type="text" class="{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}"
						name="A1_text" />
				</div>

				<div id="${_zone}_a1_zone2" style="display: none;">
					<span style="margin-right: 5px; font-weight: bold;">选择订单表:</span> <select name="tableName" class="chosen needValid {required:true}" disabled="disabled">
						<option value="">请选择</option>
						<c:forEach items="${orderTables}" var="o">
							<option value="${o.name}">[${o.name}]${o.description}</option>
						</c:forEach>
					</select>
				</div></td>
		</tr>
		<tr>
			<th rowspan="2">[A2]历史表</th>
			<td><input type="checkbox" value="1" name="A2" /><label>自动创建</label><span style="color: red;" tip="true" title="部署后可在设置界面绑定/解除绑定">(提示)</span></td>
		</tr>
		<tr>
			<td><span style="margin-right: 5px; font-weight: bold;">创建表名:</span><input type="text" disabled="disabled"
				class="{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}" name="A2_text" /></td>
		</tr>
		<tr>
			<th rowspan="2">[A3]审批意见表</th>
			<td><input type="checkbox" value="1" name="A3" /><label>自动创建</label><span style="color: red;" tip="true" title="部署后可在设置界面绑定/解除绑定">(提示)</span></td>
		</tr>
		<tr>
			<td><span style="margin-right: 5px; font-weight: bold;">创建表名:</span><input type="text" disabled="disabled"
				class="{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}" name="A3_text" /></td>
		</tr>
		<tr class="last-child">
			<th rowspan="2">[B1]基础视图</th>
			<td><input type="checkbox" value="1" name="B1" checked="checked" disabled="disabled" /><label>自动创建</label></td>
		</tr>
		<tr>
			<td><div id="${_zone}_b1_zone1">
					<span style="color: red;">(自动生成)</span>
				</div>
				<div id="${_zone}_b1_zone2" style="display: none;">
					<span style="margin-right: 5px; font-weight: bold;">选择视图:</span> <select name="basicView" class="chosen needValid {required:true}" disabled="disabled">
						<option value="">请选择</option>
					</select>
				</div></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="updateFlag" value="${column!=null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		Core.fn($zone, 'getTableZone', function(type) {
			//先把原有的table放回预备域
			$('#${_zone}_hidden_field_type_zone').append($('table.ws-table', $('#${_zone}_field_type_zone')));
			var $table = $('table[fieldType="' + type + '"]', $('#${_zone}_hidden_field_type_zone'));
			$('#${_zone}_field_type_zone').append($table);

			if ($table.attr('autoIncrement') == 'true') {
				$(':radio[name="autoIncrement"]', $zone).iCheck('enable');
			} else {
				$(':radio[name="autoIncrement"]', $zone).iCheck('disable');
			}
		});

		var updateFlag = '${updateFlag}';
		var $form = $('form', $('#${_zone}'));
		var valiForm = $form.validate({
			errorPlacement : function(error, element) { // 错误信息位置设置方法
				var $parent = element.parents(':not(.ui-spinner):first');
				error.appendTo($parent); // 这里的element是录入数据的对象
			}
		});
		$form.data("validator").settings.ignore += ':not(.chzn-done)';
		$form.on("submit", function() {
			if (valiForm.form()) {
				var jsonArray = $form.serializeArray();
				var json = {};
				$.each(jsonArray, function(i, field) {
					var key = field.name;
					var val = field.value;
					if (json[key] == null) {
						json[key] = val;
					} else {
						if (!json[key].push) {
							json[key] = [ json[key] ];
						}
						json[key].push(val);
					}
				});

				if (Core.fn('${param.parentZone}', 'addColumn')(updateFlag == 'true', json)) {//回调
					$('#${_zone}').dialog("close");
				}
			}
			return false;
		});

		$("select[name=mappedTypeCode]").on("change", function() {
			var type = this.value;
			Core.fn($zone, 'getTableZone')(type);
		});
		$('#${_zone}_hidden_field_type_zone').hide();

		//初始化数据
		if (updateFlag == 'true') {
			var column = eval('(' + $('#${_zone}_column_data', $zone).val() + ')');
			Core.fn($zone, 'getTableZone')(column.mappedTypeCode);
			$("select[name=mappedTypeCode]").val(column.mappedTypeCode).trigger('liszt:updated');
			var $form = $('form', $('#${_zone}'));
			$.each(column, function(key, value) {
				if (key == 'required' || key == 'primaryKey' || key == 'autoIncrement') {
					$(":radio[name='" + key + "'][value='" + (value ? 1 : 0) + "']", $form).iCheck("check");
				} else {
					$("[name='" + key + "']", $form).val(value);
				}
			});
			$('input[name="name"]', $form).attr('readonly', 'readonly');
		}
	});
</script>

<!-- 列数据 -->
<textarea style="display: none;" id="${_zone}_column_data">${column!=null?column:'{}'}</textarea>

<!-- 附属属性预备值 -->
<div id="${_zone}_hidden_field_type_zone">

	<!-- 大文本附加域 -->
	<table fieldType="${type.Clob}" class="ws-table">
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="text"></wcm:widget></td>
		</tr>
	</table>

	<!-- 字符附加域 -->
	<table fieldType="${type.String}" class="ws-table">
		<tr>
			<th>字符串长度</th>
			<td><wcm:widget name="totalSize" cmd="text{digits:true,min:100,max:5000}" value="100"></wcm:widget></td>
		</tr>
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="text"></wcm:widget></td>
		</tr>
	</table>

	<!-- 整形附加域 -->
	<table fieldType="${type.Integer}" class="ws-table" autoIncrement="true">
		<tr>
			<th>数字长度</th>
			<td><wcm:widget name="totalSize" cmd="text{digits:true,min:4,max:8}" value="8"></wcm:widget><input type="hidden" name="scale" value="0" /></td>
		</tr>
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="text{digits:true}"></wcm:widget></td>
		</tr>
	</table>

	<!-- 长整形附加域 -->
	<table fieldType="${type.Long}" class="ws-table" autoIncrement="true">
		<tr>
			<th>数字长度</th>
			<td><wcm:widget name="totalSize" cmd="text{digits:true,min:9,max:16}" value="9"></wcm:widget><input type="hidden" name="scale" value="0" /></td>
		</tr>
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="text{digits:true}"></wcm:widget></td>
		</tr>
	</table>

	<!-- 复数附加域 -->
	<table fieldType="${type.BigDecimal}" class="ws-table">
		<tr>
			<th>数字长度</th>
			<td><wcm:widget name="totalSize" cmd="text{digits:true,min:8,max:16}" value="8"></wcm:widget></td>
		</tr>
		<tr>
			<th>数字精度</th>
			<td><wcm:widget name="scale" cmd="text{digits:true,min:0,max:4}" value="2"></wcm:widget></td>
		</tr>
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="text{number:true}"></wcm:widget></td>
		</tr>
	</table>

	<!-- 日期时间附加域 -->
	<table fieldType="${type.Date}" class="ws-table">
		<tr>
			<th>默认值</th>
			<td><wcm:widget name="defaultValue" cmd="date[datetime]"></wcm:widget></td>
		</tr>
	</table>
</div>

<form sync="true">
	<table class="ws-table">
		<tr>
			<th>字段名</th>
			<td title="只允许使用大写英文,数字和下划线" tip="true"><wcm:widget name="name" cmd="text{required:true,maxlength:20,pattern2:['[A-Z]{1}[A-Z0-9_]*','只允许使用大写英文,数字和下划线']}"></wcm:widget></td>
		</tr>
		<tr>
			<th>展示名</th>
			<td><wcm:widget name="description" cmd="text{required:true,maxlength:20}"></wcm:widget></td>
		</tr>
		<tr>
			<th>类型</th>
			<td><wcm:widget name="mappedTypeCode" cmd="select[@com.riversoft.platform.db.Types(请选择)]{required:true}"></wcm:widget>
		</tr>
		<tr>
			<th>备注</th>
			<td><wcm:widget name="memo" cmd="textarea"></wcm:widget></td>
		</tr>

		<tr>
			<th>是否必须</th>
			<td><wcm:widget name="required" cmd="radio[YES_NO]{required:true}" value="0"></wcm:widget></td>
		</tr>
		<tr>
			<th>是否主键</th>
			<td><wcm:widget name="primaryKey" cmd="radio[YES_NO]" value="0"></wcm:widget></td>
		</tr>
		<tr>
			<th>主键是否自增长</th>
			<td><wcm:widget name="autoIncrement" cmd="radio[YES_NO]" value="0" /></td>
		</tr>
	</table>
	<!-- 附属属性 -->
	<div id="${_zone}_field_type_zone"></div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
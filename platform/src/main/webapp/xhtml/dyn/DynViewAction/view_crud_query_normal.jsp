<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('select[name=tmpName]', $zone).on("change", function() {
			var $this = $(this);
			var value = this.value;
			var $condition = $('[name=tmpCondition]', $this.parents('table'));
			if (value == '') {
				$('optgroup', $condition).attr("disabled", "disabled").trigger('liszt:updated');
			} else {
				var type = $('option[value=' + value + ']', $this).attr('type');
				type = 0 + type;
				if (type == 91 || type == 92 || type == 93) {
					type = 'date';
				} else if (type == 12 || type == 2005) {
					type = 'string';
				} else {
					type = 'number';
				}
				$('optgroup', $condition).attr("disabled", "disabled");
				$('optgroup[name=' + type + ']', $condition).removeAttr("disabled");
				$condition.val('').trigger('liszt:updated');
			}
		});

		$('select[name=tmpCondition]', $zone).on("change", function() {
			var $this = $(this);
			var value = this.value;
			var $column = $('select[name=tmpName]', $this.parents('table'));
			var $name = $("input[name$='.name']", $this.parents('table'));
			$name.val(value + '_' + $column.val());
		});

	});
</script>

<c:set var="pixel" value="${param.pixel}" />
<input type="hidden" name="${param.type}" value="${pixel}" />
<table class="ws-table">
	<tr>
		<th>查询字段</th>
		<td><select name="tmpName" class="chosen needValid {required:true}">
				<option value="">请选择</option>
				<c:forEach items="${tbTable.tbColumns}" var="tb">
					<option value="${tb.name}" type="${tb.mappedTypeCode}">[${tb.name}]${tb.description}</option>
				</c:forEach>
		</select></td>
	</tr>
	<tr>
		<th>条件</th>
		<td><select name="tmpCondition" class="chosen needValid {required:true}">
				<option value="">请选择</option>
				<optgroup label="字符串判断" name="string" disabled="disabled">
					<option value="_se">等于</option>
					<option value="_sne">不等于</option>
					<option value="_sl">模糊匹配</option>
					<option value="_snl">不匹配</option>
					<option value="_sin">包含(多选框)</option>
					<option value="_snin">不包含(多选框)</option>
				</optgroup>
				<optgroup label="数字判断" name="number" disabled="disabled">
					<option value="_ne">等于</option>
					<option value="_nne">不等于</option>
					<option value="_nb">大于</option>
					<option value="_nbe">大于或等于</option>
					<option value="_ns">小于</option>
					<option value="_nse">小于或等于</option>
					<option value="_nin">包含(多选框)</option>
					<option value="_nnin">不包含(多选框)</option>
				</optgroup>
				<optgroup label="日期判断" name="date" disabled="disabled">
					<option value="_de">等于</option>
					<option value="_dne">不等于</option>
					<option value="_dnm">小于等于</option>
					<option value="_dnl">大于等于</option>
				</optgroup>
		</select></td>
	</tr>
	<tr>
		<th>生成命令</th>
		<td><input type="text" name="${pixel}.name" class="{required:true}" readonly="readonly" /></td>
	</tr>
	<tr>
		<th>展示名</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件</th>
		<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="text"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件动态入参(脚本类型)</th>
		<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]"></wcm:widget></td>
	</tr>
	<tr>
		<th>控件动态入参(脚本)</th>
		<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]"></wcm:widget></td>
	</tr>
	<tr>
		<th>默认值</th>
		<td><wcm:widget name="${pixel}.defVal" cmd="textarea"></wcm:widget></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
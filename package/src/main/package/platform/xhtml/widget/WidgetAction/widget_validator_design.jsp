<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var json = eval('(' + $('textarea[name=validator]', $zone).val() + ')');

		if (json.required) {
			$(':radio[name=required][value=1]', $zone).iCheck('check');
		}

		var types = [ 'digits', 'number', 'url', 'email', 'integer' ];
		for (var i = 0; i < types.length; i++) {
			var type = types[i];
			if (json[type]) {
				$('input:radio[name=type][value=' + type + ']', $zone).iCheck('check');
			}
		}

		if (json.min != undefined && json.min >= 0) {
			$('input[name=min]', $zone).val(json.min);
		}
		if (json.max != undefined && json.max >= 0) {
			$('input[name=max]', $zone).val(json.max);
		}
		if (json.minlength != undefined && json.minlenth >= 0) {
			$('input[name=minlength]', $zone).val(json.minlength);
		}
		if (json.maxlength != undefined && json.maxlength >= 0) {
			$('input[name=maxlength]', $zone).val(json.maxlength);
		}

		if (json.extension != undefined) {
			$('textarea[name=extension]', $zone).val(json.extension);
		}

		if (json.pattern2 != undefined) {
			if ($.isArray(json.pattern2) && json.pattern2.lenth == 2) {
				$('textarea[name=pattern2]', $zone).val(json.pattern2[0]);
				$('textarea[name=pattern2_msg]', $zone).val(json.pattern2[1]);
			}
		}

	});
</script>

<textarea name="validator" style="display: none;">${validator}</textarea>

<table class="ws-table">
	<tr>
		<th>是否必填</th>
		<td><wcm:widget name="required" cmd="radio[YES_NO]" value="0" /></td>
	</tr>
	<tr>
		<th>基本类型验证</th>
		<td><input type="radio" value="none" name="type" checked="checked" /><label>不验证类型</label> <input type="radio" value="digits" name="type" /><label>正整数</label> <input type="radio"
			value="integer" name="type" /><label>整数</label><input type="radio" value="number" name="type" /><label>数字(含小数)</label> <input type="radio" value="url" name="type" /><label>网址</label> <input
			type="radio" value="email" name="type" /><label>Email</label></td>
	</tr>

	<tr>
		<th>字符长度</th>
		<td><input type="text" name="minlength" class="{digits:true}" value="" /> - <input type="text" name="maxlength" class="{digits:true}" value="" /></td>
	</tr>

	<tr>
		<th>数字大小</th>
		<td><input type="text" name="min" class="{digits:true}" value="" /> - <input type="text" name="max" class="{digits:true}" value="" /></td>
	</tr>

	<tr>
		<th>文件类型<br /> <font color="red">(用|分割,例:zip|rar)</font></th>
		<td><textarea rows="5" cols="5" name="extension"></textarea></td>
	</tr>

	<tr>
		<th>正则表达式<br /> <font color="red">(例:[A-Z]{1}[A-Z0-9_]*)</font></th>
		<td><textarea rows="5" cols="5" name="pattern2"></textarea></td>
	</tr>
	<tr>
		<th>正则验证错误提示</th>
		<td><textarea rows="5" cols="5" name="pattern2_msg"></textarea></td>
	</tr>
</table>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
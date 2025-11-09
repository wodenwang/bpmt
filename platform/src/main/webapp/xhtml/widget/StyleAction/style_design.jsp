<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$.each($('div.slider', $zone), function() {
			var $this = $(this);
			var name = $this.attr('name');
			var $input = $('input:hidden[name=' + name + ']', $zone);
			var $span = $input.next();
			var value = $input.val();
			if (value == '') {
				value = '0';
				$input.val(value);
			}

			$span.html(value);

			$this.slider({
				range : "min",
				min : 0,
				max : 500,
				step : 10,
				value : value,
				slide : function(event, ui) {
					$input.val(ui.value);
					$span.html(ui.value);
				}
			});
		});
	});
</script>

<form sync="true">
	<table class="ws-table">
		<tr>
			<th check="true"></th>
			<th colspan="2"></th>
		</tr>
		<tr>
			<td rowspan="2" check="true" value="min-width" checkstate="${vo['min-width']!=null}"></td>
			<th rowspan="2">宽度</th>
			<td style="font-weight: bold;"><input type="hidden" readonly="readonly" name="min-width" value="${vo['min-width']}" /><span style="color: red; margin-right: 10px;"></span>像素</td>
		</tr>
		<tr>
			<td style="padding: 10px 10px 10px 10px;">
				<div name="min-width" class="slider"></div>
			</td>
		</tr>
		<tr>
			<td rowspan="2" check="true" value="min-height" checkstate="${vo['min-height']!=null}"></td>
			<th rowspan="2">高度</th>
			<td style="font-weight: bold;"><input type="hidden" readonly="readonly" name="min-height" value="${vo['min-height']}" /><span style="color: red; margin-right: 10px;"></span>像素</td>
		</tr>
		<tr>
			<td style="padding: 10px 10px 10px 10px;">
				<div name="min-height" class="slider"></div>
			</td>
		</tr>
		<tr>
			<td check="true" value="background" checkstate="${vo['background']!=null}"></td>
			<th>背景颜色</th>
			<td><wcm:widget name="background" cmd="colorpicker" value="${vo['background']}"></wcm:widget></td>
		</tr>
		<tr>
			<td check="true" value="text-align" checkstate="${vo['text-align']!=null}"></td>
			<th>对齐方式</th>
			<td><wcm:widget name="text-align" cmd="select[STYLE_CLASS]" value="${vo['text-align']}"></wcm:widget></td>
		</tr>
		<tr>
			<td check="true" value="color" checkstate="${vo['color']!=null}"></td>
			<th>字体颜色</th>
			<td><wcm:widget name="color" cmd="colorpicker" value="${vo['color']}"></wcm:widget></td>
		</tr>
		<tr>
			<td check="true" value="font-weight" checkstate="${vo['font-weight']!=null}"></td>
			<th>字体样式</th>
			<td><wcm:widget name="font-weight" cmd="select[FONT-WEIGHT]" value="${vo['font-weight']}"></wcm:widget></td>
		</tr>
	</table>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
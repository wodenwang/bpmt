<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $sortZone = $('#${_zone}_position_zone');
		var columns = '${columns}';
		var size = new Number('${size}');
		var initValues = null;
		if (columns != '') {
			var tmpValues = columns.split(";");
			initValues = new Array();
			for (var i = 0; i < tmpValues.length - 1; i++) {
				var prevValue = (i == 0 ? 0 : initValues[i - 1]);
				initValues[i] = prevValue + new Number(tmpValues[i]);
			}
		}

		//初始化拖动框
		Core.fn($zone, 'initSort',
				function(newArray) {
					try {
						$('.ws-column', $sortZone).sortable("destroy");
					} catch (e) {
						//do nothing
					}
					$sortZone.html('');

					for (var i = 0; i < newArray.length; i++) {
						$sortZone.append('<div class="ws-column" columnIndex="' + i + '" style="width:' + newArray[i]
								+ '%;" columnWidth="' + newArray[i] + '"></div>');
					}

					//装入标签
					var $tab = $('<div class="ui-widget-content ui-corner-all ws-panel"></div>');
					$tab.append('<h3 class="ui-widget-header ui-corner-all" style="cursor: move;"></h3>').append(
							'<div></div>');

					var json = eval('(' + $('#${_zone}_homes_data').val() + ')');
					var homes = json.homes;
					$.each(homes, function(i, o) {
						var $t = $tab.clone();
						$t.attr('pixel', o.pixel);
						$('h3', $t).html(o.name);
						var columnIndex = o.columnIndex;
						if (columnIndex >= newArray.length) {
							columnIndex = newArray.length - 1;
						}
						$('div[columnIndex="' + columnIndex + '"]', $sortZone).append($t);
					});

					$(".ws-column", $sortZone).sortable({
						connectWith : ".ws-column"
					});

				});

		//转换数组
		Core.fn($zone, 'formatValues', function(values) {
			var newValues = [ 0 ].concat(values.concat([ 100 ]));//合并数组
			var newArray = new Array();
			for (var i = 1; i < newValues.length; i++) {
				newArray.push(newValues[i] - newValues[i - 1]);
			}
			return newArray;
		});

		//展示数据
		Core.fn($zone, 'showResult', function(newArray) {
			$('#${_zone}_slider_show').html('');
			for (var i = 0; i < newArray.length; i++) {
				$('#${_zone}_slider_show').append(
						'列' + (i + 1) + ':<span style="margin-right:5px;color:red;">[' + newArray[i] + ']</span>');
				$('div[columnIndex="' + i + '"]', $sortZone).css("width", newArray[i] + "%");
				$('div[columnIndex="' + i + '"]', $sortZone).attr('columnWidth', newArray[i]);
			}
		});

		//初始化
		Core.fn($zone, 'initSlider', function(size, values) {
			try {
				$("#${_zone}_slider").slider("destroy");
			} catch (e) {
				//do nothing.
			}
			var newArray;
			if (size == 1) {
				newArray = [ 100 ];
			} else {
				if (values == undefined || values == null) {
					values = new Array(size - 1);
					for (var i = 0; i < size - 1; i++) {
						values[i] = new Number(100 / size * (i + 1));
					}
				}

				$("#${_zone}_slider").slider({
					min : 10,
					max : 90,
					values : values,
					slide : function(event, ui) {
						for (var i = 0; i < ui.values.length - 1; i++) {
							var val1 = ui.values[i];
							var val2 = ui.values[i + 1];
							if (val1 >= val2) {
								return false;
							}
						}
						var newArray = Core.fn($zone, 'formatValues')(ui.values);
						Core.fn($zone, 'showResult')(newArray);
					}
				});
				newArray = Core.fn($zone, 'formatValues')($("#${_zone}_slider").slider("values"));
			}

			Core.fn($zone, 'showResult')(newArray);
			Core.fn($zone, 'initSort')(newArray);
		});

		// 分列数改变
		$('select[name=columnCount]', $zone).change(function() {
			Core.fn($zone, 'initSlider')($(this).val());
		});

		//初始化
		Core.fn($zone, 'initSlider')(size, initValues);

	});
</script>

<table class="ws-table">
	<tr>
		<th>分列数</th>
		<td><select name="columnCount" class="chosen">
				<c:forEach items="${'1,2,3,4'.split(',')}" var="i">
					<c:choose>
						<c:when test="${i==size}">
							<option value="${i}" selected="selected">${i}列</option>
						</c:when>
						<c:otherwise>
							<option value="${i}">${i}列</option>
						</c:otherwise>
					</c:choose>
				</c:forEach>
		</select></td>
	</tr>
	<tr class="last-child">
		<th rowspan="2">列宽度比例<br />(百分比)
		</th>
		<td id="${_zone}_slider_show" style="font-weight: bold;"></td>
	</tr>
	<tr>
		<td><div id="${_zone}_slider"></div></td>
	</tr>
</table>

<textarea id="${_zone}_homes_data" style="display: none;">${json}</textarea>
<%-- 位置调整区域 --%>

<div style="margin-top: 10px;" id="${_zone}_position_zone"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
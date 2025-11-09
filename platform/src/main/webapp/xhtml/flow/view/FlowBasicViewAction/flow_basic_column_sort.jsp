<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
.gridster * {
	margin: 0;
	padding: 0;
}

.gridster {
	border-style: dashed;
	border-width: 1px;
}

.gridster ul {
	list-style-type: none;
	position: relative;
}

.gridster li {
	text-align: center;
}

.gridster li.line,.gridster li.self_line {
	text-align: left !important;
}

.gridster .gs-w {
	cursor: pointer;
}

.gridster .preview-holder {
	border: none !important;
	background: red !important;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var sort = function() {
			$.each($("#${_zone}_sort_zone ul li", $zone), function() {
				var $li = $(this);
				var $text = $("textarea[name=sortColumn]", $li);
				var o = {};
				o.type = $li.attr("vo-type");
				o.id = $li.attr("vo-id");
				o.sort = new Number($li.attr("data-row")) * 1000 + new Number($li.attr("data-col"));
				$text.val(JSON.stringify(o));
			});
		};

		var maxCols = new Number("${maxCols}");
		var gridster = $("#${_zone}_sort_zone ul", $zone).gridster({
			max_cols : maxCols,
			widget_base_dimensions : [ 150, 20 ],
			widget_margins : [ 3, 3 ],
			autogrow_cols : true,
			resize : {
				enabled : false
			},
			draggable : {
				stop : function(event, ui) {
					sort();
				}
			}
		}).data('gridster');

		sort();
	});
</script>

<input type="hidden" name="hasColumnSort" value="true" />
<div id="${_zone}_sort_zone" class="gridster">
	<ul name="result">
		<c:forEach items="${list}" var="vo">
			<li class="ui-state-default ${vo.type}" vo-id="${vo.id}" vo-type="${vo.type}" data-row="${vo.row}" data-col="${vo.col}" data-sizex="${vo.sizex}" data-sizey="1"><img src="${_cp}${vo.icon}" /><font
				color="${vo.color}" style="font-weight: bold;">${vo.busiName}</font> <textarea style="display: none;" name="sortColumn"></textarea></li>
		</c:forEach>
	</ul>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
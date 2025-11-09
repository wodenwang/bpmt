<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
ul.sortList {
	list-style-type: none;
	padding: 0;
	margin-top: 40px;
	width: 100%;
	position: relative;
	overflow: hidden;
	zoom: 1;
	min-height: 120px;
}

ul.sortList li {
	margin: 5px;
	padding: 3px;
	float: left;
	position: relative;
	cursor: move;
}

.portlet-placeholder {
	border: 1px dotted black;
	margin: 0 1em 1em 0;
	height: 20px;
	width: 80px;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var sort = function() {
			var $wait = $('ul[name=wait]', $zone);
			var $result = $('ul[name=result]', $zone);
			$('textarea', $wait).prop('disabled', true);
			$('textarea', $result).prop('disabled', false);
		};

		$(".sortList", $zone).sortable({
			connectWith : ".sortList",
			cursor : 'move',
			placeholder : "portlet-placeholder",
			stop : function(event, ui) {
				sort();
			}
		});

		$("div", $zone).disableSelection();
	});
</script>

<input type="hidden" name="hasListSort" value="true" />

<div style="border-style: dashed; border-width: 1px; background-color: #ffffee;">
	<span class="ui-state-default" style="font-weight: bold; float: right;">已选字段</span>
	<ul name="result" class="sortList">
		<c:forEach items="${list}" var="vo">
			<li class="ui-state-default"><img src="${_cp}${vo.icon}" /><font color="${vo.color}" style="font-weight: bold;">${vo.busiName}</font> <textarea style="display: none;" name="listSortColumn">{id:${vo.id},type:'${vo.type}'}</textarea></li>
		</c:forEach>
	</ul>
</div>

<div style="border-style: dashed; border-width: 1px; background-color: #ffffff; margin-top: 5px;">
	<span class="ui-state-default" style="font-weight: bold; float: right;">待选字段</span>
	<ul name="wait" class="sortList">
		<c:forEach items="${waitList}" var="vo">
			<li class="ui-state-default"><img src="${_cp}${vo.icon}" /><font color="${vo.color}" style="font-weight: bold;">${vo.busiName}</font> <textarea disabled="disabled" style="display: none;"
					name="listSortColumn">{id:${vo.id},type:'${vo.type}'}</textarea></li>
		</c:forEach>
	</ul>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
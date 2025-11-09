<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $picture = $('#${_zone}_picture_zone', $zone);
		var $fo = $('textarea', $picture);

		//鼠标hover事件
		$('div.g', $zone).hover(function() {
			if (!$(this).attr('hold')) {
				$(this).css('border', '5px solid blue');
			}
		}, function() {
			if (!$(this).attr('hold')) {
				$(this).css('border', '');
			}
		});

		//单击事件
		$('div.g', $zone).click(function() {
			$('div.g', $zone).css('border', '');
			$('div.g', $zone).removeAttr('hold');
			$(this).css('border', '5px solid green');
			$(this).attr('hold', true);

			var activityId = $(this).attr('name');
			Ajax.post('${_zone}_detail', '${_acp}/nodeDetail.shtml', {
				data : {
					activityId : activityId,
					_FO : $fo.val()
				}
			});
		});

		$('img', $picture).attr('src', '${_acp}/picture.shtml?_tmp=' + Math.random() + '&_FO=' + $fo.val());

		var $currentActivity = $('#${_zone}_current_activity', $zone);
		if ($currentActivity.val() != '') {
			$('div.g[name=' + $currentActivity.val() + ']', $zone).click();
		}
	});
</script>

<input type="hidden" id="${_zone}_current_activity" name="currentActivity" value="${currentActivityId}" />

<div style="overflow: auto; zoom: 1;">
	<!-- 流程明细信息 -->
	<div style="float: right; width: 400px;" id="${_zone}_detail">
		<div class="ws-msg info">${wpf:lan("#:zh[请点击左侧流程图查看历史信息.]:en[Please click on the left side of the flow chart for historical information.]#")}</div>
	</div>

	<!-- 流程图 -->
	<div id="${_zone}_picture_zone" style="position: relative; height:  ${baseG.maxY-baseG.minY+30}px; margin-right:420px; overflow: auto; border: 1px dashed #000;">
		<textarea style="display: none;" name="_FO">${wcm:json(fo)}</textarea>
		<img style="position: absolute;" alt="流程图" />

		<%-- 坐标信息 --%>
		<c:forEach items="${gs}" var="entry">
			<c:set var="g" value="${entry.value}" />
			<div class="g" name="${entry.key}"
				style="position: absolute;cursor: pointer;width: ${g.width-4}px; height: ${g.height-4}px; left:${g.x-baseG.minX+2}px;top:${g.y-baseG.minY+2}px;border-radius: 15px;"></div>
		</c:forEach>
	</div>


</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $text = $('textarea', $zone);

		Core.fn($zone, 'initIcon', function() {
			var $resultZone = $('span[name=resultZone]', $zone);
			var icon = $text.val();
			$resultZone.html('');
			if (icon != '') {
				var $span = $('<span style="margin-right: 10px;font-weight: bold;color:blue;">图标预览:</span>');
				var $img = $('<img src="${iconCp}/'+icon+'" title="'+icon+'"/>');
				$img.attr("style", 'width: 16px; height: 16px; border-width: 0px;');
				$resultZone.append($span);
				$resultZone.append($img);
			}
		});

		//选择图标
		$('button[name=selectIcon]', $zone).click(function() {
			var $win = Ajax.win('${_acp}/sysIconList.shtml', {
				title : '请选择图标',
				minWidth : 800,
				maxHeight : 400,
				callback : function(flag) {
					//设置最大尺寸
					$win.dialog("option", "maxWidth", 800);
					$win.dialog("option", "maxHeight", 400);
				}
			});

			Core.fn($win, 'callback', function(val) {
				$text.val(val);
				$win.dialog("close");
				Core.fn($zone, 'initIcon')();
			});
		});

		$('button[name=deleteIcon]', $zone).click(function() {
			$text.val('');
			Core.fn($zone, 'initIcon')();
		});

		Core.fn($zone, 'initIcon')();
	});
</script>

<textarea name="${param.name}" style="display: none;" class="needValid ${param.validate}">${param.value}</textarea>

<span name="resultZone" style="width: 30px; margin-right: 10px;"></span>

<c:if test="${param.state!='readonly'&&param.state!='disabled'}">
	<span class="ws-group">
		<button type="button" icon="trash" name="deleteIcon" text="false">清空</button>
		<button type="button" icon="search" name="selectIcon" text="false">选择</button>
	</span>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
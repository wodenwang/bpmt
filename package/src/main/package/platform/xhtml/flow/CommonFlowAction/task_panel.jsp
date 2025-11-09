<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var callback = function() {
			//设置标题
			Ajax.json('${_acp}/getTaskCount.shtml', function(result) {
				var userCount = result.userCount;
				var title = Ui.getCurrentTitle($('#${_zone}_task'));
				var $title = $('<div>' + title + '</div>');
				if (userCount <= 0) {
					$('span.ui-state-highlight', $title).remove();
				} else if ($('span.ui-state-highlight', $title).size() > 0) {
					$('span.ui-state-highlight', $title).text('[' + userCount + ']');
				} else {
					$title.append('<span class="ui-state-highlight" style="color:red;">[' + userCount + ']</span>');
				}
				Ui.changeCurrentTitle($('#${_zone}_task'), $title.html());

				var shareCount = result.shareCount;
				var title = Ui.getCurrentTitle($('#${_zone}_share_task'));
				var $title = $('<div>' + title + '</div>');
				if (shareCount <= 0) {
					$('span.ui-state-highlight', $title).remove();
				} else if ($('span.ui-state-highlight', $title).size() > 0) {
					$('span.ui-state-highlight', $title).text('[' + shareCount + ']');
				} else {
					$title.append('<span class="ui-state-highlight" style="color:red;">[' + shareCount + ']</span>');
				}
				Ui.changeCurrentTitle($('#${_zone}_share_task'), $title.html());
			}, {
				data : {
					_params : $('#${_zone}_params').val()
				}
			});
			$('#${_zone}_task form[query=true]').submit();
			$('#${_zone}_share_task form[query=true]').submit();
		};

		Ajax.post('${_zone}_task', '${_acp}/taskMain.shtml', {
			showFlag : false,
			data : {
				_params : $('#${_zone}_params').val()
			}
		});

		Ajax.post('${_zone}_share_task', '${_acp}/taskMain.shtml', {
			showFlag : false,
			data : {
				type : 'share',
				_params : $('#${_zone}_params').val()
			}
		});

		Core.fn('${_zone}_task', 'callback', callback);
		Core.fn('${_zone}_share_task', 'callback', callback);

		callback();//初始化
	});
</script>

<textarea style="display: none;" id="${_zone}_params" name="_tmp">${param._params}</textarea>
<div tabs="true" id="${_zone}_tabs" main="true">
	<div title="${wpf:lan('#:zh[个人任务]:en[Personal tasks]#')}" id="${_zone}_task"></div>
	<div title="${wpf:lan('#:zh[群组任务]:en[Group tasks]#')}" id="${_zone}_share_task"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
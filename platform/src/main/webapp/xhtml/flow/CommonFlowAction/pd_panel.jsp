<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var refresh = function() {
			Ajax.post($zone, '${_acp}/pdPanel.shtml', {
				data : {
					_params : $('#${_zone}_params').val()
				}
			});
		};

		var invokeTask = function($target, id) {
			try {
				$target.dialog("close");//尝试关闭待办窗口
			} catch (e) {

			}
			var $win = Ajax.win('${_acp}/form.shtml', {
				minWidth : 1024,
				data : {
					_TASK_ID : id
				}
			});
			Core.fn($win, 'callback', refresh);
		};

		var invokeDetail = function($target, id) {
			try {
				$target.dialog("close");//尝试关闭待办窗口
			} catch (e) {

			}
			var $win = Ajax.win('${_acp}/detail.shtml', {
				minWidth : 1024,
				data : {
					_TASK_ID : id
				}
			});
			Core.fn($win, 'callback', refresh);
		};

		//跳转
		var taskList = function(type, pdKey, pdName) {
			try {
				$zone.dialog("close");//尝试关闭待办窗口
			} catch (e) {

			}
			var $win = Ajax.win('${_acp}/taskList.shtml', {
				minWidth : 1024,
				title : pdName,
				data : {
					type : type,
					pdKey : pdKey
				}
			});

			Core.fn($win, 'invokeTask', invokeTask);
			Core.fn($win, 'invokeDetail', invokeDetail);
		};

		$.each($('tr[pdKey]', $zone), function() {
			var $tr = $(this);
			var pdKey = $tr.attr('pdKey');
			var pdName = $tr.attr('pdName');
			Ajax.json('${_acp}/getTaskCount.shtml', function(result) {
				if (result.flag) {
					var userCount = result.userCount;
					if (userCount > 0) {
						var $td = $('td[name=userCount]', $tr);
						var $span = $('<span style="color:red;cursor: pointer;font-weight: bold;">' + userCount + '</span>');
						$td.html('');
						$td.append($span);
						$span.click(function() {
							taskList('', pdKey, pdName + '[${wpf:lan("#:zh[个人待办]:en[Personal to-do]#")}]');
						});
					}

					var shareCount = result.shareCount;
					if (shareCount > 0) {
						var $td = $('td[name=shareCount]', $tr);
						var $span = $('<span style="color:red;cursor: pointer;font-weight: bold;">' + shareCount + '</span>');
						$td.html('');
						$td.append($span);
						$span.click(function() {
							taskList('share', pdKey, pdName + '[${wpf:lan("#:zh[群组待办]:en[Group to-do]#")}]');
						});
					}
				}
			}, {
				data : {
					pdKey : pdKey
				}
			});
		});

	});
</script>

<textarea style="display: none;" id="${_zone}_params">${param._params}</textarea>
<table class="ws-table">
	<tr>
		<th style="width: 2em;">${wpf:lan("#:zh[序号]:en[Serial number]#")}</th>
		<th>${wpf:lan("#:zh[所属流程]:en[Belong to the process]#")}</th>
		<th style="width: 2em;">${wpf:lan("#:zh[个人待办]:en[Personal to-do]#")}</th>
		<th style="width: 2em;">${wpf:lan("#:zh[群组待办]:en[Group to-do]#")}</th>
	</tr>
	<c:forEach items="${list}" var="vo" varStatus="status">
		<tr pdKey="${vo.key}" pdName="${vo.name}">
			<td class="center">${status.index+1}</td>
			<td>${wpf:lan(vo.name)}</td>
			<td class="center" name="userCount">0</td>
			<td class="center" name="shareCount">0</td>
		</tr>
	</c:forEach>
</table>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
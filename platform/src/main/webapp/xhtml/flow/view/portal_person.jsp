<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $main = $zone;
		var $msg = $('#${_zone}_msg', $zone);
		var $tabs = $("#${_zone}_main_tab", $zone);

		//调用后刷新
		var invokeCallback = function() {
			//订单tab
			getTaskCount($("#${_zone}_task_my"));//获取数量
			getTaskCount($("#${_zone}_task_share"));//获取数量
			$('#${_zone}_left_tabs [tmploaded=true]', $zone).attr('tmploaded', 'false');
			$('#${_zone}_left_tabs', $zone).tabs("option", "active", 0);
		};

		//绑定tab触发事件
		$('#${_zone}_left_tabs', $zone).on("tabsactivate", function(event, ui) {
			var $panel = ui.newPanel;
			var tmploaded = $panel.attr("tmploaded");
			if (tmploaded == 'true') {
				return;
			}

			if ($panel.attr('task') == 'true') {//任务
				Ajax.post($panel, '${_acp}/listTask.shtml', {
					showFlag : false,
					data : {
						type : $panel.attr('type'),
						_params : $('#${_zone}_params').val(),
						_main : '${_zone}'
					},
					callback : function() {
						$panel.attr("tmploaded", "true");
					}
				});
			} else if ($panel.attr('order') == 'true') {//订单
				Ajax.post($panel, '${_acp}/main.shtml', {
					showFlag : false,
					data : {
						type : $panel.attr('type'),
						_params : $('#${_zone}_params').val(),
						_main : '${_zone}'
					},
					callback : function() {
						$panel.attr("tmploaded", "true");
					}
				});
			}

			//设置回调
			Core.fn($panel, 'callback', function() {
				invokeCallback();
			});

		});

		//设置标题的数字
		var setTitleCount = function($this, count) {
			var title = Ui.getCurrentTitle($this);
			var $title = $('<div>' + title + '</div>');
			if (count > 0) {
				if ($('span', $title).size() > 0) {
					$('span', $title).html('[' + count + ']');
				} else {
					$title.append('<span class="ui-state-highlight" style="color:red;">[' + count + ']</span>');
				}
			} else {
				$('span', $title).remove();
			}
			Ui.changeCurrentTitle($this, $title.html());
		};

		//获取任务数量
		var getTaskCount = function($this) {
			Ajax.json('${_acp}/countTask.shtml', function(result) {
				var count = result.count;
				setTitleCount($this, count);
			}, {
				data : {
					type : $this.attr('type')
				}
			});
		};

		//初始化
		invokeCallback();
	});
</script>

<div tabs="true" main="true" id="${_zone}_tabs">
	<div title="${wpf:lan(title)}">
		<textarea style="display: none;" id="${_zone}_params">${param._params}</textarea>
		<div id="${_zone}_msg" name="mainMsgZone"></div>
		<div tabs="true" id="${_zone}_left_tabs" button="left" active="1">
			<div title="我的发起" type="my" order="true" id="${_zone}_order_my"></div>
			<div title="我的暂存" type="draft" order="true" id="${_zone}_order_draft"></div>
			<div title="我的经办" type="relate" order="true" id="${_zone}_order_relate"></div>
			<div title="我的完成" type="close" order="true" id="${_zone}_order_close"></div>
			<div title="个人待办" task="true" type="my" id="${_zone}_task_my"></div>
			<div title="群组待办" task="true" type="share" id="${_zone}_task_share"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
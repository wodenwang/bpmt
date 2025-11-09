<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		var quickMode = '${quickMode?1:0}' == '1';

		var callback = function() {
			Core.fn($zone, 'callback')();
		};

		var invokeTask = function(id) {
			var $tab;
			if (quickMode) {
				$tab = Ajax.win('${_acp}/form.shtml', {
					title : '${wpf:lan("#:zh[处理订单]:en[Handle Order]#")}',
					minWidth : 1024,
					data : {
						_TASK_ID : id
					}
				});
			} else {
				$tab = Ajax.tab(Ui.getTab(), '${_acp}/form.shtml', {
					title : '${wpf:lan("#:zh[处理订单]:en[Handle Order]#")}',
					data : {
						_TASK_ID : id
					}
				});
			}
			Core.fn($tab, 'callback', callback);
		};

		var invokeDetail = function(id) {
			var $tab;
			if (quickMode) {
				$tab = Ajax.win('${_acp}/detail.shtml', {
					title : '${wpf:lan("#:zh[查看订单]:en[View Order]#")}',
					minWidth : 1024,
					data : {
						_TASK_ID : id
					}
				});
			} else {
				$tab = Ajax.tab(Ui.getTab(), '${_acp}/detail.shtml', {
					title : '${wpf:lan("#:zh[查看订单]:en[View Order]#")}',
					data : {
						_TASK_ID : id
					}
				});
			}
			Core.fn($tab, 'callback', callback);
		};

		Core.fn('${_zone}_list', 'invokeTask', invokeTask);
		Core.fn('${_zone}_list', 'invokeDetail', invokeDetail);

		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		$('#${_zone}_form').submit();
	});
</script>

<form query="true" id="${_zone}_form" zone="${_zone}_list" action="${_acp}/taskList.shtml">
	<input type="hidden" name="type" value="${param.type}" />
	<textarea style="display: none;" id="${_zone}_params" name="_params">${param._params}</textarea>
	<c:if test="${!quickMode}">
		<table class="ws-table" col="2">
			<tr>
				<th>${wpf:lan("#:zh[任务时间]:en[Task time]#")}(>=)</th>
				<td><wcm:widget name="_after_createTime" cmd="date[datetime]" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[任务时间]:en[Task time]#")}(<=)</th>
				<td><wcm:widget name="_before_createTime" cmd="date[datetime]" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[单号(模糊)]:en[Order No.(fuzzy)]#")}</th>
				<td><wcm:widget cmd="text" name="businessKey" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[所属流程(模糊)]:en[Belong to the process(fuzzy)]#")}</th>
				<td><wcm:widget cmd="text" name="process" /></td>
			</tr>
			<tr whole="true">
				<th class="ws-bar">
					<div class="ws-group left">
						<button type="button" icon="arrowthick-2-n-s" name="expand">${wpf:lan("#:zh[展开/收缩查询框]:en[Expansion/contraction query box]#")}</button>
					</div>
					<div class="ws-group right">
						<button type="reset" icon="arrowreturnthick-1-w" text="true">${wpf:lan("#:zh[重置查询]:en[Reset query]#")}</button>
						<button type="submit" icon="search" text="true">${wpf:lan("#:zh[查询]:en[Query]#")}</button>
					</div>
				</th>
			</tr>
			</tr>
		</table>
	</c:if>
</form>

<div id="${_zone}_list"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
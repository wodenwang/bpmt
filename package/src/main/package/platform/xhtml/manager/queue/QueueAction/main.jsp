<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//列表页
		Core.fn('${_zone}_list', 'create', function() {
			var $tab = Ui.openTab('创建异步队列', '${_acp}/createZone.shtml');
			Core.fn($tab, 'submitForm', Core.fn($zone, 'submitForm'));
		});

		Core.fn('${_zone}_list', 'del', function(key) {
			Ui.confirm('确认删除异步队列?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/delete.shtml?queueKey=' + key, {
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});

		Core.fn('${_zone}_list', 'edit', function(key) {
			var $tab = Ui.openTab('编辑', '${_acp}/updateZone.shtml?queueKey=' + key);
			Core.fn($tab, 'submitForm', Core.fn($zone, 'submitForm'));
		});

		//表单页
		Core.fn($zone, 'submitForm', function($form, $tab, option) {
			option = $.extend({}, option, {
				callback : function(flag) {
					if (flag) {
						Ui.closeTab($tab);
						$('#${_zone}_list_form').submit();
					}
				}
			});
			Ajax.form('${_zone}_msg', $form, option);
		});

		//初始化查询
		$('#${_zone}_list_form').submit();
	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="异步队列配置">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true"
			id="${_zone}_list_form" method="get">
			<input type="hidden" name="_field" value="updateDate" /> <input
				type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>队列KEY(精确)</th>
					<td><wcm:widget name="_se_queueKey" cmd="text">不支持命令</wcm:widget></td>
					<th>队列KEY(模糊)</th>
					<td><wcm:widget name="_sl_queueKey" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>描述(精确)</th>
					<td><wcm:widget name="_se_description" cmd="text">不支持命令</wcm:widget></td>
					<th>描述(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>

		<%--查询结果 --%>
		<div id="${_zone}_list"></div>

	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
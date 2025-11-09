<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//删除
		Core.fn('${_zone}_list', 'del', function(widgetKey) {
			Ui.confirmPassword('确认删除?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/delete.shtml?widgetKey=' + widgetKey, {
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});

		//权限设置
		Core.fn('${_zone}_list', 'pri', function(widgetKey) {
			var $win = Ui.openTab('新增控件', '${_acp}/priSetZone.shtml?widgetKey=' + widgetKey);
			Core.fn($win, 'submit', submitForm);
		});

		//提交表单
		var submitForm = function($tab, $form) {
			var zone = '${_zone}_msg';//信息提示区域
			var option = eval('(' + $form.attr("option") + ')');
			option = $.extend({}, {
				callback : function(flag) {
					if (flag) {//调用成功
						//关闭tab
						Ui.closeTab($tab);
						$('#${_zone}_list_form').submit();
					}
				},
				btn : $('button', $form)
			}, option);
			$.scrollTo($("#" + zone));
			Ajax.form(zone, $form, option);
		};

		//新增
		Core.fn('${_zone}_list', 'create', function() {
			var $win = Ui.openTab('新增控件', '${_acp}/createZone.shtml');
			Core.fn($win, 'submit', submitForm);
		});

		//编辑
		Core.fn('${_zone}_list', 'edit', function(widgetKey) {
			var $win = Ui.openTab('编辑控件[' + widgetKey + ']', '${_acp}/updateZone.shtml?widgetKey=' + widgetKey);
			Core.fn($win, 'submit', submitForm);
		});

		//初始化查询
		$('#${_zone}_list_form').submit();
	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="数据控件设置">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>控件主键(精确)</th>
					<td><wcm:widget name="_se_widgetKey" cmd="text" /></td>
					<th>控件主键(模糊)</th>
					<td><wcm:widget name="_sl_widgetKey" cmd="text" /></td>
				</tr>
				<tr>
					<th>标题(模糊)</th>
					<td><wcm:widget name="_sl_busiName" cmd="text" /></td>
					<th>描述(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text" /></td>
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
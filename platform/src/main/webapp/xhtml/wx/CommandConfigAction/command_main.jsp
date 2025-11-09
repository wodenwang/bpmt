<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//新增
		Core.fn('${_zone}_list_zone', 'create', function() {
			var $tab = Ui.openTab('新建处理器', '${_acp}/createZone.shtml');
			//表单页
			Core.fn($tab, 'submit', function($form) {
				Ajax.form('${_zone}_msg_zone', $form, {
					confirmMsg : '确认新建处理器?',
					errorZone : $('div[name=msgZone]', $tab).attr('id'),
					callback : function(flag) {
						if (flag) {
							Ui.closeTab($tab);
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//删除
		Core.fn('${_zone}_list_zone', 'remove', function(commandKey) {
			Ui.confirmPassword('确认删除处理器[' + commandKey + ']?', function() {
				Ajax.post('${_zone}_msg_zone', '${_acp}/remove.shtml', {
					data : {
						commandKey : commandKey
					},
					callback : function(flag) {
						if (flag) {
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//设置
		Core.fn('${_zone}_list_zone', 'edit', function(commandKey) {
			var $tab = Ui.openTab('修改处理器', '${_acp}/editZone.shtml?commandKey=' + commandKey);
			//表单页
			Core.fn($tab, 'submit', function($form) {
				Ajax.form('${_zone}_msg_zone', $form, {
					confirmMsg : '保存所做的修改?',
					errorZone : $('div[name=msgZone]', $tab).attr('id'),
					callback : function(flag) {
						if (flag) {
							Ui.closeTab($tab);
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//初始查询
		$("#${_zone}_list_form").submit();
	});
</script>

<div tabs="true" main="true" id="${_zone}_tabs">
	<div title="事件处理器">
		<form action="${_acp}/list.shtml" query="true" zone="${_zone}_list_zone" id="${_zone}_list_form">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>逻辑主键(模糊)</th>
					<td><input type="text" name="_sl_commandKey" /></td>
					<th>展示名</th>
					<td><input type="text" name="_sl_busiName" /></td>
				</tr>
				<tr>
					<th>描述</th>
					<td><input type="text" name="_sl_description" /></td>
					<th>脚本</th>
					<td><wcm:widget name="_sl_logicScript" cmd="text" /></td>
				</tr>
				<tr>

					<th>使用范围</th>
					<td><wcm:widget name="supportType" cmd="multiselect[@com.riversoft.platform.translate.WxCommandSupportType]" /></td>
					<th></th>
					<td></td>
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

		<div id="${_zone}_msg_zone"></div>
		<div id="${_zone}_list_zone"></div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>